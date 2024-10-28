package com.milesight.iab.entity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.JsonUtils;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.ExchangeFlowExecutor;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.AttachTargetType;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.security.SecurityUserContext;
import com.milesight.iab.data.filterable.Filterable;
import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.facade.IDeviceFacade;
import com.milesight.iab.entity.enums.AggregateType;
import com.milesight.iab.entity.model.dto.EntityHistoryUnionQuery;
import com.milesight.iab.entity.model.request.EntityAggregateQuery;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author loong
 * @date 2024/10/16 14:22
 */
@Service
@Slf4j
public class EntityService implements EntityServiceProvider {

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
    ExchangeFlowExecutor exchangeFlowExecutor;
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

    private EntityPO saveConvert(Entity entity, Map<String, Long> deviceKeyMap, Map<String, EntityPO> dataEntityIdMap) {
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
            EntityPO entityPO = new EntityPO();
            Long entityId = null;
            EntityPO dataEntityPO = dataEntityIdMap.get(entity.getKey());
            if (dataEntityPO == null) {
                entityId = SnowflakeUtil.nextId();
            } else {
                entityId = dataEntityPO.getId();
                entityPO.setCreatedAt(dataEntityPO.getCreatedAt());
            }
            entityPO.setId(entityId);
            entityPO.setKey(entity.getKey());
            entityPO.setName(entity.getName());
            entityPO.setType(entity.getType());
            entityPO.setAccessMod(entity.getAccessMod());
            entityPO.setParent(entity.getParentKey());
            entityPO.setAttachTarget(attachTarget);
            entityPO.setAttachTargetId(attachTargetId);
            entityPO.setValueAttribute(entity.getAttributes());
            entityPO.setValueType(entity.getValueType());
            return entityPO;
        } catch (Exception e) {
            log.error("save entity error:{}", e.getMessage(), e);
            throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
        }
    }

    @Override
    public List<Entity> findByTargetId(AttachTargetType targetType, String targetId) {
        if (!StringUtils.hasText(targetId)) {
            return new ArrayList<>();
        }
        return findByTargetIds(targetType, Collections.singletonList(targetId));
    }

    @Override
    public List<Entity> findByTargetIds(AttachTargetType targetType, List<String> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.in(EntityPO.Fields.attachTargetId, targetIds.toArray()));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, String> deviceKeyMap = new HashMap<>();
        Map<String, Integration> deviceIntegrationMap = new HashMap<>();
        if (targetType == AttachTargetType.DEVICE) {
            List<DeviceNameDTO> deviceNameDTOList = deviceFacade.getDeviceNameByIds(targetIds.stream().map(Long::valueOf).distinct().collect(Collectors.toList()));
            if (deviceNameDTOList != null && !deviceNameDTOList.isEmpty()) {
                deviceKeyMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getKey)));
                deviceIntegrationMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getIntegrationConfig)));
            }
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
                    entity.setValueType(childEntityPO.getValueType());
                    entity.setType(childEntityPO.getType());
                    entity.setAttributes(childEntityPO.getValueAttribute());
                    String parentKey = childEntityPO.getParent();
                    if (StringUtils.hasText(parentKey)) {
                        parentKey = parentKey.substring(parentKey.lastIndexOf(".") + 1);
                    }
                    entity.setParentIdentifier(parentKey);
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
                entity.setValueType(entityPO.getValueType());
                entity.setType(entityPO.getType());
                entity.setAttributes(entityPO.getValueAttribute());
                String parentKey = entityPO.getParent();
                if (StringUtils.hasText(parentKey)) {
                    parentKey = parentKey.substring(parentKey.lastIndexOf(".") + 1);
                }
                entity.setParentIdentifier(parentKey);
                List<Entity> childEntityList = childrenEntityList.stream().filter(childEntity -> entity.getIdentifier().equals(childEntity.getParentIdentifier())).toList();
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
        List<Entity> allEntityList = new ArrayList<>();
        allEntityList.add(entity);
        allEntityList.addAll(entity.getChildren());

        doBatchSaveEntity(allEntityList);
    }

    @Override
    public void batchSave(List<Entity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return;
        }
        List<Entity> allEntityList = new ArrayList<>();
        allEntityList.addAll(entityList);
        entityList.forEach(entity -> {
            List<Entity> childrenEntityList = entity.getChildren();
            if (childrenEntityList != null && !childrenEntityList.isEmpty()) {
                allEntityList.addAll(childrenEntityList);
            }
        });
        doBatchSaveEntity(allEntityList);
    }

    private void doBatchSaveEntity(List<Entity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return;
        }
        List<String> deviceKeys = entityList.stream().map(Entity::getDeviceKey).filter(StringUtils::hasText).toList();
        Map<String, Long> deviceKeyMap = new HashMap<>();
        if (!deviceKeys.isEmpty()) {
            List<DeviceNameDTO> deviceNameDTOList = deviceFacade.getDeviceNameByKey(deviceKeys);
            if (deviceNameDTOList != null && !deviceNameDTOList.isEmpty()) {
                deviceKeyMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(DeviceNameDTO::getKey, DeviceNameDTO::getId)));
            }
        }
        List<String> entityKeys = entityList.stream().map(Entity::getKey).filter(StringUtils::hasText).toList();
        List<EntityPO> dataEntityPOList = getByKeys(entityKeys);
        Map<String, EntityPO> dataEntityIdMap = new HashMap<>();
        if (dataEntityPOList != null && !dataEntityPOList.isEmpty()) {
            dataEntityIdMap.putAll(dataEntityPOList.stream().collect(Collectors.toMap(EntityPO::getKey, Function.identity())));
        }
        List<EntityPO> entityPOList = new ArrayList<>();
        entityList.forEach(t -> {
            EntityPO entityPO = saveConvert(t, deviceKeyMap, dataEntityIdMap);
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
            List<EntityPO> deviceEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.DEVICE).in(EntityPO.Fields.attachTargetId, deviceIds.toArray()));
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
            List<EntityPO> deviceEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.DEVICE).in(EntityPO.Fields.attachTargetId, deviceIds.toArray()));
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
        List<EntityPO> integrationEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.INTEGRATION).in(EntityPO.Fields.attachTargetId, integrationIds.toArray()));
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
        List<EntityLatestPO> nowEntityLatestPOList = entityLatestRepository.findAll(filter -> filter.in(EntityLatestPO.Fields.entityId, entityIds.toArray()));
        Map<Long, EntityLatestPO> entityIdDataMap = new HashMap<>();
        if (nowEntityLatestPOList != null && !nowEntityLatestPOList.isEmpty()) {
            entityIdDataMap.putAll(nowEntityLatestPOList.stream().collect(Collectors.toMap(EntityLatestPO::getEntityId, Function.identity())));
        }
        List<EntityLatestPO> entityLatestPOList = new ArrayList<>();
        payloads.forEach((entityKey, payload) -> {
            Long entityId = entityIdMap.get(entityKey);
            if (entityId == null) {
                return;
            }
            EntityLatestPO dataEntityLatest = entityIdDataMap.get(entityId);
            Long entityLatestId = null;
            if (dataEntityLatest == null) {
                entityLatestId = SnowflakeUtil.nextId();
            } else {
                if (dataEntityLatest.getTimestamp() >= exchangePayload.getTimestamp()) {
                    log.info("entityLatestId is bigger than exchangePayload timestamp, entityId:{}, exchangePayload:{}", entityId, exchangePayload);
                    return;
                }
                entityLatestId = dataEntityLatest.getId();
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
            entityLatestPO.setTimestamp(exchangePayload.getTimestamp());
            entityLatestPOList.add(entityLatestPO);
        });
        entityLatestRepository.saveAll(entityLatestPOList);
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
                entityHistoryPO.setCreatedBy(operatorId);
            }
            entityHistoryPO.setUpdatedBy(operatorId);
            entityHistoryPOList.add(entityHistoryPO);
        });
        entityHistoryRepository.saveAll(entityHistoryPOList);
    }

    @Override
    public JsonNode findExchangeValueByKey(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        return findExchangeValuesByKeys(Collections.singletonList(key));
    }

    @Override
    public JsonNode findExchangeValuesByKeys(List<String> keys) {
        List<EntityPO> allEntities = new ArrayList<>();
        List<EntityPO> entityPOList = getByKeys(keys);
        List<EntityPO> childrenEntities = getByParentKeys(keys);
        if (entityPOList != null && !entityPOList.isEmpty()) {
            allEntities.addAll(entityPOList);
        }
        if (childrenEntities != null && !childrenEntities.isEmpty()) {
            allEntities.addAll(childrenEntities);
        }
        if (allEntities.isEmpty()) {
            return null;
        }
        List<Long> entityIds = allEntities.stream().map(EntityPO::getId).toList();
        Map<Long, String> entityKeyMap = allEntities.stream().collect(Collectors.toMap(EntityPO::getId, EntityPO::getKey));
        List<EntityLatestPO> entityLatestPOList = entityLatestRepository.findAll(filter -> filter.in(EntityLatestPO.Fields.entityId, entityIds.toArray()));
        if (entityLatestPOList == null || entityLatestPOList.isEmpty()) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();
        entityLatestPOList.forEach(entityLatestPO -> {
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
            resultMap.put(entityKeyMap.get(entityLatestPO.getEntityId()), value);
        });
        return JsonUtils.toJsonNode(resultMap);
    }

    @Override
    public <T> T findExchangeByKey(String key, Class<T> entitiesClazz) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        JsonNode exchangeValueNode = findExchangeValueByKey(key);
        if (exchangeValueNode == null || exchangeValueNode.isEmpty()) {
            return null;
        }
        try {
            T instance = entitiesClazz.getDeclaredConstructor().newInstance();
            Field[] fields = entitiesClazz.getDeclaredFields();

            exchangeValueNode.fieldNames().forEachRemaining(exchangeKey -> {
                try {
                    for (Field field : fields) {
                        String keyFieldName = exchangeKey.substring(exchangeKey.lastIndexOf(".") + 1);
                        if (field.getName().equals(keyFieldName)) {
                            field.setAccessible(true);
                            field.set(instance, JsonUtils.cast(exchangeValueNode.get(exchangeKey), field.getType()));
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.error("findExchangeByKey error:{}", e.getMessage(), e);
                }
            });
            return instance;
        } catch (Exception e) {
            log.error("findExchangeByKey error:{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Entity findByKey(String entityKey) {
        Map<String, Entity> entityMap = findByKeys(entityKey);
        return entityMap.get(entityKey);
    }

    @Override
    public Map<String, Entity> findByKeys(String... entityKeys) {
        if (entityKeys == null || entityKeys.length == 0) {
            return new HashMap<>();
        }
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return new HashMap<>();
        }
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
                    entity.setValueType(childEntityPO.getValueType());
                    entity.setType(childEntityPO.getType());
                    entity.setAttributes(childEntityPO.getValueAttribute());
                    String parentKey = childEntityPO.getParent();
                    if (StringUtils.hasText(parentKey)) {
                        parentKey = parentKey.substring(parentKey.lastIndexOf(".") + 1);
                    }
                    entity.setParentIdentifier(parentKey);
                    childrenEntityList.add(entity);
                } catch (Exception e) {
                    log.error("find entity by targetId error:{}", e.getMessage(), e);
                    throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
                }
            });
        }
        List<Entity> entityList = new ArrayList<>();
        List<Long> deviceIds = entityPOList.stream().filter(entityPO -> entityPO.getAttachTarget() == AttachTargetType.DEVICE).map(t -> Long.valueOf(t.getAttachTargetId())).distinct().toList();
        Map<String, String> deviceKeyMap = new HashMap<>();
        Map<String, Integration> deviceIntegrationMap = new HashMap<>();
        if (!deviceIds.isEmpty()) {
            List<DeviceNameDTO> deviceNameDTOList = deviceFacade.getDeviceNameByIds(deviceIds);
            if (deviceNameDTOList != null && !deviceNameDTOList.isEmpty()) {
                deviceKeyMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getKey)));
                deviceIntegrationMap.putAll(deviceNameDTOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getIntegrationConfig)));
            }
        }
        entityPOList.forEach(entityPO -> {
            try {
                Entity entity = new Entity();
                entity.setId(entityPO.getId());
                entity.setName(entityPO.getName());
                entity.setIdentifier(entityPO.getKey().substring(entityPO.getKey().lastIndexOf(".") + 1));
                entity.setAccessMod(entityPO.getAccessMod());
                entity.setValueType(entityPO.getValueType());
                entity.setType(entityPO.getType());
                entity.setAttributes(entityPO.getValueAttribute());
                String parentKey = entityPO.getParent();
                if (StringUtils.hasText(parentKey)) {
                    parentKey = parentKey.substring(parentKey.lastIndexOf(".") + 1);
                }
                entity.setParentIdentifier(parentKey);
                List<Entity> childEntityList = childrenEntityList.stream().filter(childEntity -> entity.getIdentifier().equals(childEntity.getParentIdentifier())).toList();
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
        return entityList.stream().collect(Collectors.toMap(Entity::getKey, Function.identity()));
    }

    public Page<EntityResponse> search(EntityQuery entityQuery) {
        boolean isExcludeChildren = entityQuery.getExcludeChildren() != null && entityQuery.getExcludeChildren();
        List<String> attachTargetIds = new ArrayList<>();
        if (StringUtils.hasText(entityQuery.getKeyword())) {
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
        }
        Consumer<Filterable> filterable = f -> f.eq(entityQuery.getEntityType() != null, EntityPO.Fields.type, entityQuery.getEntityType())
                .eq(isExcludeChildren, EntityPO.Fields.parent, null)
                .or(f1 -> f1.like(StringUtils.hasText(entityQuery.getKeyword()), EntityPO.Fields.name, entityQuery.getKeyword())
                        .in(StringUtils.hasText(entityQuery.getKeyword()), EntityPO.Fields.attachTargetId, attachTargetIds.toArray()));
        Page<EntityPO> entityPOList = entityRepository.findAll(filterable, entityQuery.toPageable());
        if (entityPOList == null || entityPOList.getContent().isEmpty()) {
            return Page.empty();
        }
        List<Long> resultDeviceIds = entityPOList.stream().filter(entityPO -> entityPO.getAttachTarget() == AttachTargetType.DEVICE).map(t -> Long.parseLong(t.getAttachTargetId())).distinct().toList();
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
            response.setEntityType(entityPO.getType());
            response.setEntityName(entityPO.getName());
            response.setEntityValueAttribute(entityPO.getValueAttribute());
            response.setEntityValueType(entityPO.getValueType().name());
            return response;
        });
    }

    public List<EntityResponse> getChildren(Long entityId) {
        EntityPO entityPO = entityRepository.findOne(f -> f.eq(EntityPO.Fields.id, entityId)).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).build());
        List<EntityPO> entityPOList = entityRepository.findAll(f -> f.eq(EntityPO.Fields.parent, entityPO.getKey()));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> resultDeviceIds = entityPOList.stream().filter(t -> t.getAttachTarget() == AttachTargetType.DEVICE).map(t -> Long.parseLong(t.getAttachTargetId())).distinct().toList();
        List<DeviceNameDTO> resultDevices = deviceFacade.getDeviceNameByIds(resultDeviceIds);
        Map<String, String> deviceNameMap = new HashMap<>();
        Map<String, String> deviceIntegrationNameMap = new HashMap<>();
        if (resultDevices != null && !resultDevices.isEmpty()) {
            deviceNameMap.putAll(resultDevices.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), DeviceNameDTO::getName)));
            deviceIntegrationNameMap.putAll(resultDevices.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), t -> t.getIntegrationConfig().getName())));
        }
        return entityPOList.stream().map(t -> {
            EntityResponse response = new EntityResponse();
            String deviceName = null;
            String integrationName = null;
            if (t.getAttachTarget() == AttachTargetType.DEVICE) {
                String deviceId = t.getAttachTargetId();
                deviceName = deviceNameMap.get(deviceId);
                integrationName = deviceIntegrationNameMap.get(deviceId);
            } else if (t.getAttachTarget() == AttachTargetType.INTEGRATION) {
                String integrationId = t.getAttachTargetId();
                Integration integration = integrationServiceProvider.getIntegration(integrationId);
                integrationName = integration.getName();
            }
            response.setDeviceName(deviceName);
            response.setIntegrationName(integrationName);
            response.setEntityId(t.getId().toString());
            response.setEntityAccessMod(t.getAccessMod());
            response.setEntityKey(t.getKey());
            response.setEntityType(t.getType());
            response.setEntityName(t.getName());
            response.setEntityValueAttribute(t.getValueAttribute());
            response.setEntityValueType(t.getValueType().name());
            return response;
        }).collect(Collectors.toList());
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
        EntityLatestPO entityLatestPO = entityLatestRepository.findOne(filter -> filter.eq(EntityLatestPO.Fields.entityId, entityId)).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).build());
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
        entityLatestResponse.setUpdatedAt(entityLatestPO.getUpdatedAt() == null ? null : entityLatestPO.getUpdatedAt().toString());
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

    public void updatePropertyEntity(UpdatePropertyEntityRequest updatePropertyEntityRequest) {
        Map<String, Object> exchange = updatePropertyEntityRequest.getExchange();
        List<String> entityKeys = exchange.keySet().stream().toList();
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys.toArray()));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return;
        }
        if (entityPOList.stream().anyMatch(entityPO -> !entityPO.getType().equals(EntityType.PROPERTY))) {
            log.info("update property entity only support property entity");
            return;
        }
        if (entityPOList.stream().anyMatch(entityPO -> entityPO.getAccessMod() != AccessMod.RW && entityPO.getAccessMod() != AccessMod.W)) {
            log.info("update property entity only support write access");
            return;
        }
        ExchangePayload payload = new ExchangePayload(exchange);
        payload.putContext(SecurityUserContext.USER_ID, SecurityUserContext.getUserId());
        exchangeFlowExecutor.asyncExchangeDown(payload);
    }

    public void serviceCall(ServiceCallRequest serviceCallRequest) {
        Map<String, Object> exchange = serviceCallRequest.getExchange();
        List<String> entityKeys = exchange.keySet().stream().toList();
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys.toArray()));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return;
        }
        if (entityPOList.stream().anyMatch(entityPO -> !entityPO.getType().equals(EntityType.SERVICE))) {
            log.info("service call only support service entity");
            return;
        }
        ExchangePayload payload = new ExchangePayload(exchange);
        exchangeFlowExecutor.syncExchangeDown(payload);
    }

    private EntityPO getByKey(String entityKey) {
        return entityRepository.findOne(filter -> filter.eq(EntityPO.Fields.key, entityKey)).orElse(null);
    }

    private List<EntityPO> getByParentKey(String entityKey) {
        return entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.parent, entityKey));
    }

    private List<EntityPO> getByKeys(List<String> entityKeys) {
        return entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys.toArray()));
    }

    private List<EntityPO> getByParentKeys(List<String> entityKeys) {
        return entityRepository.findAll(filter -> filter.in(EntityPO.Fields.parent, entityKeys.toArray()));
    }

}
