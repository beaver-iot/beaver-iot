package com.milesight.iab.entity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.context.security.SecurityUserContext;
import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.facade.IDeviceFacade;
import com.milesight.iab.entity.enums.AggregateType;
import com.milesight.iab.entity.enums.AttachTargetType;
import com.milesight.iab.entity.model.dto.EntityHistoryUnionQuery;
import com.milesight.iab.entity.model.request.EntityAggregateQuery;
import com.milesight.iab.entity.model.request.EntityFormRequest;
import com.milesight.iab.entity.model.request.EntityHistoryQuery;
import com.milesight.iab.entity.model.request.EntityQuery;
import com.milesight.iab.entity.model.request.ServiceCallRequest;
import com.milesight.iab.entity.model.request.UpdatePropertyEntityRequest;
import com.milesight.iab.entity.model.response.EntityAggregateResponse;
import com.milesight.iab.entity.model.response.EntityHistoryResponse;
import com.milesight.iab.entity.model.response.EntityLatestResponse;
import com.milesight.iab.entity.model.response.EntityMetaResponse;
import com.milesight.iab.entity.model.response.EntityResponse;
import com.milesight.iab.entity.po.EntityHistoryPO;
import com.milesight.iab.entity.po.EntityLatestPO;
import com.milesight.iab.entity.po.EntityPO;
import com.milesight.iab.entity.repository.EntityHistoryRepository;
import com.milesight.iab.entity.repository.EntityLatestRepository;
import com.milesight.iab.entity.repository.EntityRepository;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.rule.RuleEngineExecutor;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Autowired
    IDeviceFacade deviceFacade;
    @Lazy
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;
    @Lazy
    @Autowired
    DeviceServiceProvider deviceServiceProvider;
    @Autowired
    RuleEngineExecutor engineExecutor;
    @Autowired
    EventBus eventBus;
    @Autowired
    private EntityManager entityManager;

    private final Comparator<Byte[]> byteArrayComparator = (a, b) -> {
        if (a == b) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int cmp = Byte.compare(a[i], b[i]);
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.length, b.length);
    };

    private EntityPO saveConvert(Entity entity, Map<String, Long> deviceKeyMap) {
        try {
            AttachTargetType attachTarget;
            String attachTargetId;
            if (StringUtils.hasText(entity.getDeviceKey())) {
                attachTarget = AttachTargetType.DEVICE;
                attachTargetId = deviceKeyMap.get(entity.getDeviceKey()) == null ? null : String.valueOf(deviceKeyMap.get(entity.getDeviceKey()));
                if (attachTargetId == null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                }
            } else {
                attachTarget = AttachTargetType.INTEGRATION;
                attachTargetId = entity.getIntegrationId();
            }
            Long entityId = entity.getId();
            boolean isCreate = entityId == null;
            if (entityId == null) {
                entityId = SnowflakeUtil.nextId();
            }
            EntityPO entityPO = new EntityPO();
            entityPO.setId(entityId);
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
            if (isCreate) {
                entityPO.setCreatedAt(System.currentTimeMillis());
            }
            entityPO.setUpdatedAt(System.currentTimeMillis());
            return entityPO;
        } catch (Exception e) {
            log.error("save entity error:{}", e.getMessage(), e);
            throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
        }
    }

    @Override
    public List<Entity> findByTargetId(String targetId) {
        if (!StringUtils.hasText(targetId)) {
            return new ArrayList<>();
        }
        return findByTargetIds(Collections.singletonList(targetId));
    }

    @Override
    public List<Entity> findByTargetIds(List<String> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.in(EntityPO.Fields.attachTargetId, targetIds));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<DeviceNameDTO> deviceNameDTOList = deviceFacade.getDeviceNameByIds(targetIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        Map<String, String> deviceKeyMap = new HashMap<>();
        Map<String, Integration> deviceIntegrationMap = new HashMap<>();
        if (deviceNameDTOList != null && !deviceNameDTOList.isEmpty()) {
            deviceKeyMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getKey)));
            deviceIntegrationMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getIntegrationConfig)));
        }
        List<EntityPO> parentEntityPOList = entityPOList.stream().filter(entityPO -> !StringUtils.hasText(entityPO.getParent())).toList();
        List<EntityPO> childrenEntityPOList = entityPOList.stream().filter(entityPO -> StringUtils.hasText(entityPO.getParent())).toList();
        List<Entity> childrenEntityList = new ArrayList<>();
        if (!childrenEntityPOList.isEmpty()) {
            childrenEntityPOList.forEach(childEntityPO -> {
                try {
                    Entity entity = new Entity();
                    entity.setId(childEntityPO.getId());
                    entity.setName(childEntityPO.getName());
                    entity.setIdentifier(childEntityPO.getKey().substring(childEntityPO.getKey().lastIndexOf(".") + 1));
                    entity.setAccessMod(childEntityPO.getAccessMod());
                    entity.setSyncCall(childEntityPO.getSyncCall());
                    entity.setValueType(childEntityPO.getValueType());
                    entity.setType(childEntityPO.getType());
                    entity.setAttributes(objectMapper.readValue(childEntityPO.getValueAttribute(), Map.class));
                    entity.setParentIdentifier(childEntityPO.getParent());
                    childrenEntityList.add(entity);
                } catch (Exception e) {
                    log.error("find entity by targetId error:{}", e.getMessage(), e);
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                }
            });
        }
        List<Entity> entityList = new ArrayList<>();
        parentEntityPOList.forEach(entityPO -> {
            try {
                Entity entity = new Entity();
                entity.setId(entityPO.getId());
                entity.setName(entityPO.getName());
                entity.setIdentifier(entityPO.getKey().substring(entityPO.getKey().lastIndexOf(".") + 1));
                entity.setAccessMod(entityPO.getAccessMod());
                entity.setSyncCall(entityPO.getSyncCall());
                entity.setValueType(entityPO.getValueType());
                entity.setType(entityPO.getType());
                entity.setAttributes(objectMapper.readValue(entityPO.getValueAttribute(), Map.class));
                entity.setParentIdentifier(entityPO.getParent());
                String key = entityPO.getKey();
                List<Entity> childEntityList = childrenEntityList.stream().filter(childEntity -> key.contains(childEntity.getParentIdentifier())).toList();
                entity.setChildren(childEntityList);

                String attachTargetId = entityPO.getAttachTargetId();
                AttachTargetType attachTarget = entityPO.getAttachTarget();
                if (attachTarget == AttachTargetType.DEVICE) {
                    String deviceKey = deviceKeyMap.get(attachTargetId);
                    String integrationId = null;
                    Integration deviceIntegration = deviceIntegrationMap.get(attachTargetId);
                    if (deviceIntegration != null) {
                        integrationId = deviceIntegration.getId();
                    }
                    entity.initializeProperties(integrationId, deviceKey);
                } else if (attachTarget == AttachTargetType.INTEGRATION) {
                    entity.initializeProperties(attachTargetId);
                }
                entityList.add(entity);
            } catch (Exception e) {
                log.error("find entity by targetId error:{}", e.getMessage(), e);
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
            }
        });
        return entityList;
    }

    @Override
    public void save(Entity entity) {
        if (entity == null) {
            return;
        }
        Map<String, Long> deviceKeyMap = new HashMap<>();
        if (StringUtils.hasText(entity.getDeviceKey())) {
            DeviceNameDTO deviceNameDTO = deviceFacade.getDeviceNameByKey(entity.getDeviceKey());
            if (deviceNameDTO != null) {
                deviceKeyMap.put(entity.getDeviceKey(), deviceNameDTO.getId());
            }
        }
        EntityPO entityPO = saveConvert(entity, deviceKeyMap);
        entityRepository.save(entityPO);
    }

    @Override
    public void batchSave(List<Entity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return;
        }
        Map<String, Long> deviceKeyMap = new HashMap<>();
        List<String> deviceKeys = entityList.stream().map(Entity::getDeviceKey).filter(StringUtils::hasText).toList();
        if (!deviceKeys.isEmpty()) {
            List<DeviceNameDTO> deviceNameDTOList = deviceFacade.getDeviceNameByKey(deviceKeys);
            if (deviceNameDTOList != null && !deviceNameDTOList.isEmpty()) {
                deviceKeyMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(DeviceNameDTO::getKey, DeviceNameDTO::getId)));
            }
        }
        List<EntityPO> entityPOList = new ArrayList<>();
        entityList.forEach(entity -> {
            EntityPO entityPO = saveConvert(entity, deviceKeyMap);
            entityPOList.add(entityPO);
        });
        entityRepository.saveAll(entityPOList);
    }

    @Override
    public void deleteByTargetId(String targetId) {
        if (!StringUtils.hasText(targetId)) {
            return;
        }
        entityRepository.deleteByTargetId(targetId);
    }

    @Override
    public long countAllEntitiesByIntegrationId(String integrationId) {
        if (!StringUtils.hasText(integrationId)) {
            return 0L;
        }
        long allEntityCount = 0L;
        long integrationEntityCount = countIntegrationEntitiesByIntegrationId(integrationId);
        allEntityCount += integrationEntityCount;
        List<Device> integrationDevices = deviceServiceProvider.findAll(integrationId);
        if (integrationDevices != null && !integrationDevices.isEmpty()) {
            List<String> deviceIds = integrationDevices.stream().map(t -> String.valueOf(t.getId())).toList();
            List<EntityPO> deviceEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.DEVICE).in(EntityPO.Fields.attachTargetId, deviceIds));
            if (deviceEntityPOList != null && !deviceEntityPOList.isEmpty()) {
                allEntityCount += deviceEntityPOList.size();
            }
        }
        return allEntityCount;
    }

    @Override
    public Map<String, Long> countAllEntitiesByIntegrationIds(List<String> integrationIds) {
        if (integrationIds == null || integrationIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Long> allEntityCountMap = new HashMap<>();
        allEntityCountMap.putAll(countIntegrationEntitiesByIntegrationIds(integrationIds));
        List<DeviceNameDTO> integrationDevices = deviceFacade.getDeviceNameByIntegrations(integrationIds);
        if (integrationDevices != null && !integrationDevices.isEmpty()) {
            List<String> deviceIds = integrationDevices.stream().map(DeviceNameDTO::getId).map(String::valueOf).toList();
            List<EntityPO> deviceEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.DEVICE).in(EntityPO.Fields.attachTargetId, deviceIds));
            if (deviceEntityPOList != null && !deviceEntityPOList.isEmpty()) {
                allEntityCountMap.putAll(deviceEntityPOList.stream().collect(Collectors.groupingBy(EntityPO::getAttachTargetId, Collectors.counting())));
            }
        }
        return allEntityCountMap;
    }

    @Override
    public long countIntegrationEntitiesByIntegrationId(String integrationId) {
        if (!StringUtils.hasText(integrationId)) {
            return 0L;
        }
        List<EntityPO> integrationEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.INTEGRATION).eq(EntityPO.Fields.attachTargetId, integrationId));
        if (integrationEntityPOList == null || integrationEntityPOList.isEmpty()) {
            return 0L;
        }
        return integrationEntityPOList.size();
    }

    @Override
    public Map<String, Long> countIntegrationEntitiesByIntegrationIds(List<String> integrationIds) {
        if (integrationIds == null || integrationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<EntityPO> integrationEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.INTEGRATION).in(EntityPO.Fields.attachTargetId, integrationIds));
        if (integrationEntityPOList == null || integrationEntityPOList.isEmpty()) {
            return Collections.emptyMap();
        }
        return integrationEntityPOList.stream().collect(Collectors.groupingBy(EntityPO::getAttachTargetId, Collectors.counting()));
    }

    @Override
    public void saveExchange(ExchangePayload exchangePayload) {
        if (exchangePayload == null) {
            return;
        }
        Map<String, Object> payloads = exchangePayload.getAllPayloads();
        if (payloads == null || payloads.isEmpty()) {
            return;
        }
        List<String> entityKeys = payloads.keySet().stream().toList();
        List<EntityPO> entityPOList = getByKeys(entityKeys);
        if (entityPOList == null || entityPOList.isEmpty()) {
            return;
        }
        Map<String, Long> entityIdMap = entityPOList.stream().collect(Collectors.toMap(EntityPO::getKey, EntityPO::getId));
        List<Long> entityIds = entityPOList.stream().map(EntityPO::getId).toList();
        List<EntityLatestPO> nowEntityLatestPOList = entityLatestRepository.findAll(filter -> filter.in(EntityLatestPO.Fields.entityId, entityIds));
        Map<Long, Long> entityIdDataMap = new HashMap<>();
        if (nowEntityLatestPOList != null && !nowEntityLatestPOList.isEmpty()) {
            entityIdDataMap.putAll(nowEntityLatestPOList.stream().collect(Collectors.toMap(EntityLatestPO::getEntityId, EntityLatestPO::getId)));
        }
        List<EntityLatestPO> entityLatestPOList = new ArrayList<>();
        payloads.forEach((entityKey, payload) -> {
            Long entityId = entityIdMap.get(entityKey);
            if (entityId == null) {
                return;
            }
            Long entityLatestId = entityIdDataMap.get(entityId);
            if (entityLatestId == null) {
                entityLatestId = SnowflakeUtil.nextId();
            }
            EntityLatestPO entityLatestPO = new EntityLatestPO();
            entityLatestPO.setId(entityLatestId);
            entityLatestPO.setEntityId(entityId);
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
            } else {
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
            }
            entityLatestPO.setUpdatedAt(System.currentTimeMillis());
            entityLatestPOList.add(entityLatestPO);
        });
        entityLatestRepository.saveAll(entityLatestPOList);

        eventBus.publish(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, exchangePayload));
    }

    @Override
    public void saveExchangeHistory(ExchangePayload exchangePayload) {
        if (exchangePayload == null) {
            return;
        }
        Map<String, Object> payloads = exchangePayload.getAllPayloads();
        if (payloads == null || payloads.isEmpty()) {
            return;
        }
        List<String> entityKeys = payloads.keySet().stream().toList();
        List<EntityPO> entityPOList = getByKeys(entityKeys);
        if (entityPOList == null || entityPOList.isEmpty()) {
            return;
        }
        Map<String, Long> entityIdMap = entityPOList.stream().collect(Collectors.toMap(EntityPO::getKey, EntityPO::getId));
        List<EntityHistoryUnionQuery> entityHistoryUnionQueryList = payloads.keySet().stream().map(o -> {
            Long entityId = entityIdMap.get(o);
            if (entityId == null) {
                return null;
            }
            EntityHistoryUnionQuery entityHistoryUnionQuery = new EntityHistoryUnionQuery();
            entityHistoryUnionQuery.setEntityId(entityId);
            entityHistoryUnionQuery.setTimestamp(exchangePayload.getTimestamp());
            return entityHistoryUnionQuery;
        }).filter(Objects::nonNull).toList();
        List<EntityHistoryPO> existEntityHistoryPOList = entityHistoryRepository.findByUnionUnique(entityManager, entityHistoryUnionQueryList);
        Map<String, Long> existUnionIdMap = new HashMap<>();
        if (existEntityHistoryPOList != null && !existEntityHistoryPOList.isEmpty()) {
            existUnionIdMap.putAll(existEntityHistoryPOList.stream().collect(Collectors.toMap(entityHistoryPO -> entityHistoryPO.getEntityId() + ":" + entityHistoryPO.getTimestamp(), EntityHistoryPO::getId)));
        }
        List<EntityHistoryPO> entityHistoryPOList = new ArrayList<>();
        payloads.forEach((entityKey, payload) -> {
            Long entityId = entityIdMap.get(entityKey);
            if (entityId == null) {
                return;
            }
            String unionId = entityId + ":" + exchangePayload.getTimestamp();
            Long historyId = existUnionIdMap.get(unionId);
            boolean isCreate = historyId == null;
            if (historyId == null) {
                historyId = SnowflakeUtil.nextId();
            }
            EntityHistoryPO entityHistoryPO = new EntityHistoryPO();
            entityHistoryPO.setId(historyId);
            entityHistoryPO.setEntityId(entityId);
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
            } else {
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
            }
            entityHistoryPO.setTimestamp(exchangePayload.getTimestamp());
            String operatorId = SecurityUserContext.getUserId();
            if (isCreate) {
                entityHistoryPO.setCreatedAt(System.currentTimeMillis());
                entityHistoryPO.setCreatedBy(operatorId);
            }
            entityHistoryPO.setUpdatedAt(System.currentTimeMillis());
            entityHistoryPO.setUpdatedBy(operatorId);
            entityHistoryPOList.add(entityHistoryPO);
        });
        entityHistoryRepository.saveAll(entityHistoryPOList);

        eventBus.publish(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, exchangePayload));
    }

    @Override
    public Object findExchangeValueByKey(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        EntityPO entityPO = getByKey(key);
        if (entityPO == null) {
            return null;
        }
        Long entityId = entityPO.getId();
        EntityLatestPO entityLatestPO = entityLatestRepository.findOne(filter -> filter.eq(EntityLatestPO.Fields.entityId, entityId)).orElse(null);
        if (entityLatestPO == null) {
            return null;
        }
        Object value = null;
        if (entityLatestPO.getValueBoolean() != null) {
            value = entityLatestPO.getValueBoolean();
        } else if (entityLatestPO.getValueInt() != null) {
            value = entityLatestPO.getValueInt();
        } else if (entityLatestPO.getValueFloat() != null) {
            value = entityLatestPO.getValueFloat();
        } else if (entityLatestPO.getValueString() != null) {
            value = entityLatestPO.getValueString();
        } else if (entityLatestPO.getValueBinary() != null) {
            value = entityLatestPO.getValueBinary();
        }
        return value;
    }

    @Override
    public <T> T findExchangeByKey(String key, Class<T> entitiesClazz) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        Object value = findExchangeValueByKey(key);
        try {
            T instance = entitiesClazz.getDeclaredConstructor().newInstance();
            Field[] fields = entitiesClazz.getDeclaredFields();
            for (Field field : fields) {
                String keyFieldName = key.substring(key.lastIndexOf(".") + 1);
                if (field.getName().equals(keyFieldName)) {
                    field.setAccessible(true);
                    field.set(instance, value);
                    break;
                }
            }
            return instance;
        } catch (Exception e) {
            log.error("findExchangeByKey error:{}", e.getMessage(), e);
            return null;
        }
    }

    public Page<EntityResponse> search(EntityQuery entityQuery) {
        if (!StringUtils.hasText(entityQuery.getKeyword())) {
            return Page.empty();
        }
        List<String> attachTargetIds = new ArrayList<>();
        List<Integration> integrations = integrationServiceProvider.findIntegrations(f -> f.getName().contains(entityQuery.getKeyword()));
        if (integrations != null && !integrations.isEmpty()) {
            List<String> integrationIds = integrations.stream().map(Integration::getId).toList();
            attachTargetIds.addAll(integrationIds);
            List<DeviceNameDTO> integrationDevices = deviceFacade.getDeviceNameByIntegrations(integrationIds);
            if (integrationDevices != null && !integrationDevices.isEmpty()) {
                List<String> deviceIds = integrationDevices.stream().map(t -> String.valueOf(t.getId())).toList();
                attachTargetIds.addAll(deviceIds);
            }
        }
        List<DeviceNameDTO> deviceNameDTOList = deviceFacade.fuzzySearchDeviceByName(entityQuery.getKeyword());
        if (deviceNameDTOList != null && !deviceNameDTOList.isEmpty()) {
            List<String> deviceIds = deviceNameDTOList.stream().map(DeviceNameDTO::getId).map(String::valueOf).toList();
            attachTargetIds.addAll(deviceIds);
        }
        Page<EntityPO> entityPOList = entityRepository.findAll(f -> f.eq(EntityPO.Fields.type, entityQuery.getEntityType())
                        .or(f1 -> f1.like(EntityPO.Fields.name, entityQuery.getKeyword())
                                .in(EntityPO.Fields.attachTargetId, attachTargetIds)),
                entityQuery.toPageable());
        if (entityPOList == null || entityPOList.getContent().isEmpty()) {
            return Page.empty();
        }
        List<Long> resultDeviceIds = entityPOList.stream().filter(entityPO -> entityPO.getAttachTarget() == AttachTargetType.DEVICE).map(t -> Long.parseLong(t.getAttachTargetId())).toList();
        List<DeviceNameDTO> resultDevices = deviceFacade.getDeviceNameByIds(resultDeviceIds);
        Map<String, String> deviceNameMap = new HashMap<>();
        Map<String, String> deviceIntegrationNameMap = new HashMap<>();
        if (resultDevices != null && !resultDevices.isEmpty()) {
            deviceNameMap.putAll(resultDevices.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getName)));
            deviceIntegrationNameMap.putAll(resultDevices.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), t -> t.getIntegrationConfig().getName())));
        }
        return entityPOList.map(entityPO -> {
            EntityResponse response = new EntityResponse();
            String deviceName = null;
            String integrationName = null;
            if (entityPO.getAttachTarget() == AttachTargetType.DEVICE) {
                String deviceId = entityPO.getAttachTargetId();
                deviceName = deviceNameMap.get(deviceId);
                integrationName = deviceIntegrationNameMap.get(deviceId);
            } else if (entityPO.getAttachTarget() == AttachTargetType.INTEGRATION) {
                String integrationId = entityPO.getAttachTargetId();
                Integration integration = integrationServiceProvider.getIntegration(integrationId);
                integrationName = integration.getName();
            }
            response.setDeviceName(deviceName);
            response.setIntegrationName(integrationName);
            response.setEntityId(entityPO.getId().toString());
            response.setEntityAccessMod(entityPO.getAccessMod());
            response.setEntityKey(entityPO.getKey());
            response.setEntityName(entityPO.getName());
            response.setEntityValueAttribute(entityPO.getValueAttribute());
            response.setEntityValueType(entityPO.getValueType().name());
            return response;
        });
    }

    public Page<EntityHistoryResponse> historySearch(EntityHistoryQuery entityHistoryQuery) {
        Page<EntityHistoryPO> entityHistoryPage = entityHistoryRepository.findAll(f -> f.eq(EntityHistoryPO.Fields.entityId, entityHistoryQuery.getEntityId())
                        .ge(EntityHistoryPO.Fields.timestamp, entityHistoryQuery.getStartTimestamp())
                        .le(EntityHistoryPO.Fields.timestamp, entityHistoryQuery.getEndTimestamp()),
                entityHistoryQuery.toPageable());
        if (entityHistoryPage == null || entityHistoryPage.getContent().isEmpty()) {
            return Page.empty();
        }
        return entityHistoryPage.map(entityHistoryPO -> {
            EntityHistoryResponse response = new EntityHistoryResponse();
            if (entityHistoryPO.getValueBoolean() != null) {
                response.setValue(entityHistoryPO.getValueBoolean());
            } else if (entityHistoryPO.getValueInt() != null) {
                response.setValue(entityHistoryPO.getValueInt());
            } else if (entityHistoryPO.getValueFloat() != null) {
                response.setValue(entityHistoryPO.getValueFloat());
            } else if (entityHistoryPO.getValueString() != null) {
                response.setValue(entityHistoryPO.getValueString());
            } else if (entityHistoryPO.getValueBinary() != null) {
                response.setValue(entityHistoryPO.getValueBinary());
            }
            response.setTimestamp(entityHistoryPO.getTimestamp().toString());
            return response;
        });
    }

    public EntityAggregateResponse historyAggregate(EntityAggregateQuery entityAggregateQuery) {
        List<EntityHistoryPO> entityHistoryPOList = entityHistoryRepository.findAll(filter -> filter.eq(EntityHistoryPO.Fields.entityId, entityAggregateQuery.getEntityId())
                        .ge(EntityHistoryPO.Fields.timestamp, entityAggregateQuery.getStartTimestamp())
                        .le(EntityHistoryPO.Fields.timestamp, entityAggregateQuery.getEndTimestamp()))
                .stream().sorted(Comparator.comparingLong(EntityHistoryPO::getTimestamp)).toList();
        EntityAggregateResponse entityAggregateResponse = new EntityAggregateResponse();
        if (entityHistoryPOList.isEmpty()) {
            return entityAggregateResponse;
        }
        AggregateType aggregateType = entityAggregateQuery.getAggregateType();
        EntityHistoryPO oneEntityHistoryPO = entityHistoryPOList.get(0);
        switch (aggregateType) {
            case LAST:
                EntityHistoryPO lastEntityHistoryPO = entityHistoryPOList.get(entityHistoryPOList.size() - 1);
                if (lastEntityHistoryPO.getValueBoolean() != null) {
                    entityAggregateResponse.setValue(lastEntityHistoryPO.getValueBoolean());
                } else if (lastEntityHistoryPO.getValueInt() != null) {
                    entityAggregateResponse.setValue(lastEntityHistoryPO.getValueInt());
                } else if (lastEntityHistoryPO.getValueFloat() != null) {
                    entityAggregateResponse.setValue(lastEntityHistoryPO.getValueFloat());
                } else if (lastEntityHistoryPO.getValueString() != null) {
                    entityAggregateResponse.setValue(lastEntityHistoryPO.getValueString());
                } else if (lastEntityHistoryPO.getValueBinary() != null) {
                    entityAggregateResponse.setValue(lastEntityHistoryPO.getValueBinary());
                }
                break;
            case MIN:
                if (oneEntityHistoryPO.getValueBoolean() != null) {
                    EntityHistoryPO minEntityHistoryPO = entityHistoryPOList.stream().min(Comparator.comparing(EntityHistoryPO::getValueBoolean)).get();
                    entityAggregateResponse.setValue(minEntityHistoryPO.getValueBoolean());
                } else if (oneEntityHistoryPO.getValueInt() != null) {
                    EntityHistoryPO minEntityHistoryPO = entityHistoryPOList.stream().min(Comparator.comparing(EntityHistoryPO::getValueInt)).get();
                    entityAggregateResponse.setValue(minEntityHistoryPO.getValueInt());
                } else if (oneEntityHistoryPO.getValueFloat() != null) {
                    EntityHistoryPO minEntityHistoryPO = entityHistoryPOList.stream().min(Comparator.comparing(EntityHistoryPO::getValueFloat)).get();
                    entityAggregateResponse.setValue(minEntityHistoryPO.getValueFloat());
                } else if (oneEntityHistoryPO.getValueString() != null) {
                    EntityHistoryPO minEntityHistoryPO = entityHistoryPOList.stream().min(Comparator.comparing(EntityHistoryPO::getValueString)).get();
                    entityAggregateResponse.setValue(minEntityHistoryPO.getValueString());
                } else if (oneEntityHistoryPO.getValueBinary() != null) {
                    EntityHistoryPO minEntityHistoryPO = entityHistoryPOList.stream().min(Comparator.comparing(EntityHistoryPO::getValueBinary, byteArrayComparator)).get();
                    entityAggregateResponse.setValue(minEntityHistoryPO.getValueBinary());
                }
                break;
            case MAX:
                if (oneEntityHistoryPO.getValueBoolean() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().max(Comparator.comparing(EntityHistoryPO::getValueBoolean)).get().getValueBoolean());
                } else if (oneEntityHistoryPO.getValueInt() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().max(Comparator.comparing(EntityHistoryPO::getValueInt)).get().getValueInt());
                } else if (oneEntityHistoryPO.getValueFloat() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().max(Comparator.comparing(EntityHistoryPO::getValueFloat)).get().getValueFloat());
                } else if (oneEntityHistoryPO.getValueString() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().max(Comparator.comparing(EntityHistoryPO::getValueString)).get().getValueString());
                } else if (oneEntityHistoryPO.getValueBinary() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().max(Comparator.comparing(EntityHistoryPO::getValueBinary, byteArrayComparator)).get().getValueBinary());
                }
                break;
            case AVG:
                if (oneEntityHistoryPO.getValueBoolean() != null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                } else if (oneEntityHistoryPO.getValueInt() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().mapToInt(EntityHistoryPO::getValueInt).average());
                } else if (oneEntityHistoryPO.getValueFloat() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().mapToDouble(EntityHistoryPO::getValueFloat).average());
                } else if (oneEntityHistoryPO.getValueString() != null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                } else if (oneEntityHistoryPO.getValueBinary() != null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                }
                break;
            case SUM:
                if (oneEntityHistoryPO.getValueBoolean() != null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                } else if (oneEntityHistoryPO.getValueInt() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().mapToInt(EntityHistoryPO::getValueInt).sum());
                } else if (oneEntityHistoryPO.getValueFloat() != null) {
                    entityAggregateResponse.setValue(entityHistoryPOList.stream().mapToDouble(EntityHistoryPO::getValueFloat).sum());
                } else if (oneEntityHistoryPO.getValueString() != null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                } else if (oneEntityHistoryPO.getValueBinary() != null) {
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                }
                break;
            case COUNT:
                break;
            default:
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
        }
        if (aggregateType == AggregateType.COUNT) {
            List<EntityAggregateResponse.CountResult> countResult = new ArrayList<>();
            if (oneEntityHistoryPO.getValueBoolean() != null) {
                Map<Boolean, Long> entityHistoryPOGroup = entityHistoryPOList.stream().collect(Collectors.groupingBy(EntityHistoryPO::getValueBoolean, Collectors.counting()));
                entityHistoryPOGroup.forEach((key, value) -> countResult.add(new EntityAggregateResponse.CountResult(key, value)));
            } else if (oneEntityHistoryPO.getValueInt() != null) {
                Map<Integer, Long> entityHistoryPOGroup = entityHistoryPOList.stream().collect(Collectors.groupingBy(EntityHistoryPO::getValueInt, Collectors.counting()));
                entityHistoryPOGroup.forEach((key, value) -> countResult.add(new EntityAggregateResponse.CountResult(key, value)));
            } else if (oneEntityHistoryPO.getValueFloat() != null) {
                Map<Float, Long> entityHistoryPOGroup = entityHistoryPOList.stream().collect(Collectors.groupingBy(EntityHistoryPO::getValueFloat, Collectors.counting()));
                entityHistoryPOGroup.forEach((key, value) -> countResult.add(new EntityAggregateResponse.CountResult(key, value)));
            } else if (oneEntityHistoryPO.getValueString() != null) {
                Map<String, Long> entityHistoryPOGroup = entityHistoryPOList.stream().collect(Collectors.groupingBy(EntityHistoryPO::getValueString, Collectors.counting()));
                entityHistoryPOGroup.forEach((key, value) -> countResult.add(new EntityAggregateResponse.CountResult(key, value)));
            } else if (oneEntityHistoryPO.getValueBinary() != null) {
                Map<Byte[], Long> entityHistoryPOGroup = entityHistoryPOList.stream().collect(Collectors.groupingBy(EntityHistoryPO::getValueBinary, Collectors.counting()));
                entityHistoryPOGroup.forEach((key, value) -> countResult.add(new EntityAggregateResponse.CountResult(key, value)));
            }
            entityAggregateResponse.setCountResult(countResult);
        }
        return entityAggregateResponse;
    }

    public EntityLatestResponse getEntityStatus(Long entityId) {
        EntityLatestPO entityLatestPO = entityLatestRepository.findOne(filter -> filter.eq(EntityLatestPO.Fields.entityId, entityId)).orElseThrow(()->ServiceException.with(ErrorCode.DATA_NO_FOUND).build());
        EntityLatestResponse entityLatestResponse = new EntityLatestResponse();
        if (entityLatestPO.getValueBoolean() != null) {
            entityLatestResponse.setValue(entityLatestPO.getValueBoolean());
        } else if (entityLatestPO.getValueInt() != null) {
            entityLatestResponse.setValue(entityLatestPO.getValueInt());
        } else if (entityLatestPO.getValueFloat() != null) {
            entityLatestResponse.setValue(entityLatestPO.getValueFloat());
        } else if (entityLatestPO.getValueString() != null) {
            entityLatestResponse.setValue(entityLatestPO.getValueString());
        } else if (entityLatestPO.getValueBinary() != null) {
            entityLatestResponse.setValue(entityLatestPO.getValueBinary());
        }
        entityLatestResponse.setUpdateAt(entityLatestPO.getUpdatedAt().toString());
        return entityLatestResponse;
    }

    public EntityMetaResponse getEntityMeta(Long entityId) {
        EntityPO entityPO = entityRepository.getOne(entityId);
        EntityMetaResponse entityMetaResponse = new EntityMetaResponse();
        entityMetaResponse.setKey(entityPO.getKey());
        entityMetaResponse.setName(entityPO.getName());
        entityMetaResponse.setType(entityPO.getType());
        entityMetaResponse.setAccessMod(entityPO.getAccessMod());
        entityMetaResponse.setValueAttribute(entityPO.getValueAttribute());
        entityMetaResponse.setValueType(entityPO.getValueType());
        return entityMetaResponse;
    }

    public Map<String, Object> getEntityForm(EntityFormRequest entityFormRequest) {
        List<String> entityIds = entityFormRequest.getEntityIdList();
        if (entityIds == null || entityIds.isEmpty()) {
            throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).detailMessage("entityIdList is empty").build();
        }
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.in(EntityPO.Fields.id, entityIds));
        if (entityPOList == null || entityPOList.isEmpty()) {
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("entity not found").build();
        }
        //TODO
        return null;
    }

    public void updatePropertyEntity(UpdatePropertyEntityRequest updatePropertyEntityRequest) {
        Long entityId = updatePropertyEntityRequest.getEntityId();
        Map<String, Object> exchange = updatePropertyEntityRequest.getExchange();
        EntityPO entityPO = entityRepository.getOne(entityId);
        if (entityPO == null) {
            return;
        }
        if (!entityPO.getType().equals(EntityType.PROPERTY)) {
            log.info("update property entity only support property entity:{}", entityId);
            return;
        }
        if (entityPO.getAccessMod() != AccessMod.W && entityPO.getAccessMod() != AccessMod.RW) {
            log.info("update property entity only support write access:{}", entityId);
            return;
        }
        ExchangePayload payload = new ExchangePayload(exchange);
        payload.getContext().put(SecurityUserContext.USER_ID, SecurityUserContext.getUserId());
        engineExecutor.exchangeDown(payload);
    }

    public void serviceCall(ServiceCallRequest serviceCallRequest) {
        Long entityId = serviceCallRequest.getEntityId();
        Map<String, Object> exchange = serviceCallRequest.getExchange();
        EntityPO entityPO = entityRepository.getOne(entityId);
        if (entityPO == null) {
            return;
        }
        if (!entityPO.getType().equals(EntityType.SERVICE)) {
            log.info("service call only support service entity:{}", entityId);
            return;
        }
        ExchangePayload payload = new ExchangePayload(exchange);
        engineExecutor.exchangeDown(payload);
    }

    private EntityPO getByKey(String entityKey) {
        return entityRepository.findOne(filter -> filter.eq(EntityPO.Fields.key, entityKey)).orElse(null);
    }

    private List<EntityPO> getByKeys(List<String> entityKeys) {
        return entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys));
    }

}
