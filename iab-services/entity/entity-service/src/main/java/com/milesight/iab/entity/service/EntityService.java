package com.milesight.iab.entity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.security.SecurityUserContext;
import com.milesight.iab.entity.enums.AttachTargetType;
import com.milesight.iab.entity.model.request.EntityHistoryQuery;
import com.milesight.iab.entity.model.request.EntityQuery;
import com.milesight.iab.entity.model.request.ServiceCallRequest;
import com.milesight.iab.entity.model.request.UpdatePropertyEntityRequest;
import com.milesight.iab.entity.model.response.EntityHistoryResponse;
import com.milesight.iab.entity.model.response.EntityLatestResponse;
import com.milesight.iab.entity.model.response.EntityResponse;
import com.milesight.iab.entity.po.EntityHistoryPO;
import com.milesight.iab.entity.po.EntityLatestPO;
import com.milesight.iab.entity.po.EntityPO;
import com.milesight.iab.entity.repository.EntityHistoryRepository;
import com.milesight.iab.entity.repository.EntityLatestRepository;
import com.milesight.iab.entity.repository.EntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author loong
 * @date 2024/10/16 14:22
 */
@Service
@Slf4j
public class EntityService implements EntityServiceProvider {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private EntityHistoryRepository entityHistoryRepository;
    @Autowired
    private EntityLatestRepository entityLatestRepository;

    private EntityPO saveConvert(Entity entity) {
        try {
            AttachTargetType attachTarget;
            String attachTargetId;
            if(entity.getDevice() != null){
                attachTarget = AttachTargetType.DEVICE;
                attachTargetId = String.valueOf(entity.getDevice().getId());
            }else{
                attachTarget = AttachTargetType.INTEGRATION;
                attachTargetId = entity.getIntegration().getName();
            }
            EntityPO entityPO = new EntityPO();
            entityPO.setId(SnowflakeUtil.nextId());
            entityPO.setKey(entity.getKey());
            entityPO.setName(entity.getName());
            entityPO.setType(entity.getType());
            entityPO.setAccessMod(entity.getAccessMod());
            entityPO.setSyncCall(entity.isSyncCall());
            entityPO.setParent(entity.getParentIdentifier());
            entityPO.setAttachTarget(attachTarget);
            entityPO.setAttachTargetId(attachTargetId);
            entityPO.setValueAttribute(objectMapper.writeValueAsString(entity.getAttributes()));
            entityPO.setValueType(entity.getValueType());
            entityPO.setCreatedAt(System.currentTimeMillis());
            return entityPO;
        }catch (Exception e){
            log.error("save entity error:{}", e.getMessage(),e);
            throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
        }
    }

    @Override
    public List<Entity> findByTargetId(String targetId) {
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTargetId, targetId));
        if(entityPOList == null || entityPOList.isEmpty()){
            return new ArrayList<>();
        }
        //TODO
        return null;
    }

    @Override
    public void save(Entity entity) {
        EntityPO entityPO = saveConvert(entity);
        entityRepository.save(entityPO);
    }

    @Override
    public void batchSave(List<Entity> entityList) {
        if(entityList == null || entityList.isEmpty()){
            return;
        }
        List<EntityPO> entityPOList = new ArrayList<>();
        entityList.forEach(entity -> {
            EntityPO entityPO = saveConvert(entity);
            entityPOList.add(entityPO);
        });
        entityRepository.saveAll(entityPOList);
    }

    @Override
    public void saveExchange(ExchangePayload exchangePayloadList) {
        Map<String, Object> payloads = exchangePayloadList.getAllPayloads();
        if(payloads == null || payloads.isEmpty()){
            return;
        }
        List<String> entityKeys = payloads.keySet().stream().toList();
        List<EntityPO> entityPOList = getByKeys(entityKeys);
        if(entityPOList == null || entityPOList.isEmpty()){
            return;
        }
        Map<String,Long> entityIdMap = entityPOList.stream().collect(Collectors.toMap(EntityPO::getKey, EntityPO::getId));
        List<EntityLatestPO> entityLatestPOList = new ArrayList<>();
        payloads.forEach((entityKey, payload) -> {
            EntityLatestPO entityLatestPO = new EntityLatestPO();
            entityLatestPO.setId(SnowflakeUtil.nextId());
            entityLatestPO.setEntityId(entityIdMap.get(entityKey));
            if (payload instanceof Boolean) {
                entityLatestPO.setValueBoolean((Boolean) payload);
            } else if (payload instanceof Integer) {
                entityLatestPO.setValueInt((Integer) payload);
            } else if (payload instanceof String) {
                entityLatestPO.setValueString((String) payload);
            } else if (payload instanceof Float) {
                entityLatestPO.setValueFloat((Float) payload);
            } else if (payload instanceof Byte[]) {
                entityLatestPO.setValueBinary((Byte[]) payload);
            } else{
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
            }
            entityLatestPO.setUpdatedAt(System.currentTimeMillis());
            entityLatestPOList.add(entityLatestPO);
        });
        entityLatestRepository.saveAll(entityLatestPOList);
    }

    @Override
    public void saveExchangeHistory(ExchangePayload exchangePayloadList) {
        Map<String, Object> payloads = exchangePayloadList.getAllPayloads();
        if(payloads == null || payloads.isEmpty()){
            return;
        }
        List<String> entityKeys = payloads.keySet().stream().toList();
        List<EntityPO> entityPOList = getByKeys(entityKeys);
        if(entityPOList == null || entityPOList.isEmpty()){
            return;
        }
        Map<String,Long> entityIdMap = entityPOList.stream().collect(Collectors.toMap(EntityPO::getKey, EntityPO::getId));
        List<EntityHistoryPO> entityHistoryPOList = new ArrayList<>();
        payloads.forEach((entityKey, payload) -> {
            EntityHistoryPO entityHistoryPO = new EntityHistoryPO();
            entityHistoryPO.setId(SnowflakeUtil.nextId());
            entityHistoryPO.setEntityId(entityIdMap.get(entityKey));
            if (payload instanceof Boolean) {
                entityHistoryPO.setValueBoolean((Boolean) payload);
            } else if (payload instanceof Integer) {
                entityHistoryPO.setValueInt((Integer) payload);
            } else if (payload instanceof String) {
                entityHistoryPO.setValueString((String) payload);
            } else if (payload instanceof Float) {
                entityHistoryPO.setValueFloat((Float) payload);
            } else if (payload instanceof Byte[]) {
                entityHistoryPO.setValueBinary((Byte[]) payload);
            } else{
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
            }
            entityHistoryPO.setTimestamp(exchangePayloadList.getTimestamp());
            entityHistoryPO.setCreatedAt(System.currentTimeMillis());
            SecurityUserContext.SecurityUser securityUser = SecurityUserContext.getSecurityUser();
            String createdBy = null;
            if(securityUser != null){
                createdBy = securityUser.getPayload().get("userId").toString();
            }
            entityHistoryPO.setCreatedBy(createdBy);
            entityHistoryPOList.add(entityHistoryPO);
        });
        entityHistoryRepository.saveAll(entityHistoryPOList);
    }

    @Override
    public Entity findByIdentifier(String identifier) {
        //TODO
        return null;
    }

    @Override
    public List<Entity> findAllByIntegration(String integration) {
        //TODO
        return null;
    }

    @Override
    public <T> T findByKey(String key, Class<T> entitiesClazz) {
        //TODO
        return null;
    }

    public Page<EntityResponse> search(EntityQuery entityQuery) {
        entityRepository.findAll(f -> f.like(EntityPO.Fields.name, entityQuery.getKeyword()), entityQuery.toPageable());
        //TODO
        return null;
    }

    public Page<EntityHistoryResponse> historySearch(EntityHistoryQuery entityHistoryQuery) {
        //TODO
        return null;
    }

    public EntityLatestResponse getEntityStatus(Long entityId) {
        //TODO
        return null;
    }

    public void updateEntity(UpdatePropertyEntityRequest updatePropertyEntityRequest) {
        //TODO
    }

    public void callService(ServiceCallRequest serviceCallRequest) {
        //TODO
    }

    private EntityPO getByKey(String entityKey) {
        return entityRepository.findUniqueOne(filter -> filter.eq(EntityPO.Fields.key, entityKey));
    }

    private List<EntityPO> getByKeys(List<String> entityKeys) {
        return entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys));
    }

}
