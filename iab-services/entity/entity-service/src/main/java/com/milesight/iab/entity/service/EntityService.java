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
import com.milesight.iab.context.security.SecurityUserContext;
import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.facade.IDeviceFacade;
import com.milesight.iab.entity.enums.AggregateType;
import com.milesight.iab.entity.enums.AttachTargetType;
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
import com.milesight.iab.rule.RuleEngineExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
    @Autowired
    IDeviceFacade deviceFacade;
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;
    @Autowired
    DeviceServiceProvider deviceServiceProvider;
    @Autowired
    RuleEngineExecutor engineExecutor;

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
            entityPO.setCreatedAt(System.currentTimeMillis());
            return entityPO;
        } catch (Exception e) {
            log.error("save entity error:{}", e.getMessage(), e);
            throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
        }
    }

    @Override
    public List<Entity> findByTargetId(String targetId) {
        List<EntityPO> entityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTargetId, targetId));
        if (entityPOList == null || entityPOList.isEmpty()) {
            return new ArrayList<>();
        }
        //TODO
        return null;
    }

    @Override
    public void save(Entity entity) {
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
        entityRepository.deleteByTargetId(targetId);
    }

    @Override
    public long countAllEntitiesByIntegration(String integrationId) {
        long allEntityCount = 0L;
        long integrationEntityCount = countIntegrationEntitiesByIntegration(integrationId);
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
    public long countIntegrationEntitiesByIntegration(String integrationId) {
        List<EntityPO> integrationEntityPOList = entityRepository.findAll(filter -> filter.eq(EntityPO.Fields.attachTarget, AttachTargetType.INTEGRATION).eq(EntityPO.Fields.attachTargetId, integrationId));
        if (integrationEntityPOList == null || integrationEntityPOList.isEmpty()) {
            return 0L;
        }
        return integrationEntityPOList.size();
    }

    @Override
    public void saveExchange(ExchangePayload exchangePayloadList) {
        Map<String, Object> payloads = exchangePayloadList.getAllPayloads();
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
        Map<Long, Long> entityIdNowMap = new HashMap<>();
        if (nowEntityLatestPOList != null && !nowEntityLatestPOList.isEmpty()) {
            entityIdNowMap.putAll(nowEntityLatestPOList.stream().collect(Collectors.toMap(EntityLatestPO::getEntityId, EntityLatestPO::getId)));
        }
        List<EntityLatestPO> entityLatestPOList = new ArrayList<>();
        payloads.forEach((entityKey, payload) -> {
            Long entityId = entityIdMap.get(entityKey);
            if (entityId == null) {
                return;
            }
            Long entityLatestId = entityIdNowMap.get(entityId);
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
    }

    @Override
    public void saveExchangeHistory(ExchangePayload exchangePayloadList) {
        Map<String, Object> payloads = exchangePayloadList.getAllPayloads();
        if (payloads == null || payloads.isEmpty()) {
            return;
        }
        List<String> entityKeys = payloads.keySet().stream().toList();
        List<EntityPO> entityPOList = getByKeys(entityKeys);
        if (entityPOList == null || entityPOList.isEmpty()) {
            return;
        }
        Map<String, Long> entityIdMap = entityPOList.stream().collect(Collectors.toMap(EntityPO::getKey, EntityPO::getId));
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
            } else {
                throw ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
            }
            entityHistoryPO.setTimestamp(exchangePayloadList.getTimestamp());
            entityHistoryPO.setCreatedAt(System.currentTimeMillis());
            SecurityUserContext.SecurityUser securityUser = SecurityUserContext.getSecurityUser();
            String createdBy = null;
            if (securityUser != null) {
                createdBy = securityUser.getPayload().get("userId").toString();
            }
            entityHistoryPO.setCreatedBy(createdBy);
            entityHistoryPOList.add(entityHistoryPO);
        });
        entityHistoryRepository.saveAll(entityHistoryPOList);
    }

    @Override
    public Object findExchangeValueByKey(String key) {
        EntityPO entityPO = getByKey(key);
        if (entityPO == null) {
            return null;
        }
        Long entityId = entityPO.getId();
        EntityLatestPO entityLatestPO = entityLatestRepository.findUniqueOne(filter -> filter.eq(EntityLatestPO.Fields.entityId, entityId));
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
        EntityLatestPO entityLatestPO = entityLatestRepository.findUniqueOne(filter -> filter.eq(EntityLatestPO.Fields.entityId, entityId));
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
        return entityRepository.findUniqueOne(filter -> filter.eq(EntityPO.Fields.key, entityKey));
    }

    private List<EntityPO> getByKeys(List<String> entityKeys) {
        return entityRepository.findAll(filter -> filter.in(EntityPO.Fields.key, entityKeys));
    }

}
