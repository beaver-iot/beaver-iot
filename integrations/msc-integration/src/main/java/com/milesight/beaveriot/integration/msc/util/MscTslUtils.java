package com.milesight.beaveriot.integration.msc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.milesight.cloud.sdk.client.model.ThingSpec;
import com.milesight.cloud.sdk.client.model.TslDataSpec;
import com.milesight.cloud.sdk.client.model.TslEventSpec;
import com.milesight.cloud.sdk.client.model.TslKeyValuePair;
import com.milesight.cloud.sdk.client.model.TslParamSpec;
import com.milesight.cloud.sdk.client.model.TslPropertySpec;
import com.milesight.cloud.sdk.client.model.TslServiceSpec;
import com.milesight.beaveriot.context.integration.model.AttributeBuilder;
import com.milesight.beaveriot.context.integration.model.EntityBuilder;
import com.milesight.beaveriot.context.integration.enums.AccessMod;
import com.milesight.beaveriot.context.integration.enums.EntityValueType;
import com.milesight.beaveriot.context.integration.model.Entity;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class MscTslUtils {

    private MscTslUtils() {
        throw new IllegalStateException("Utility class");
    }

    @Nonnull
    public static List<Entity> thingSpecificationToEntities(@NonNull String integrationId, @NonNull String deviceKey, @NonNull ThingSpec thingSpec) {
        val entities = new ArrayList<Entity>();
        entities.addAll(getPropertiesEntities(integrationId, deviceKey, thingSpec));
        entities.addAll(getEventEntities(integrationId, deviceKey, thingSpec));
        entities.addAll(getServiceEntities(integrationId, deviceKey, thingSpec));
        return entities;
    }

    static List<Entity> getPropertiesEntities(String integrationId, String deviceKey, ThingSpec thingSpec) {
        val entities = new ArrayList<Entity>();
        val properties = thingSpec.getProperties();
        if (properties == null || properties.isEmpty()) {
            return entities;
        }
        val propertiesGroup = properties.stream()
                .collect(Collectors.groupingBy(spec -> getRootParentId(spec.getId(), spec.getDataSpec().getParentId())));

        propertiesGroup.forEach((key, specs) -> {
            val parent = specs.stream()
                    .filter(spec -> key.equals(spec.getId()))
                    .findFirst()
                    .orElseThrow(NullPointerException::new);
            val children = specs.stream()
                    .filter(spec -> !key.equals(spec.getId()))
                    .toList();
            val parentDataType = convertDataTypeToEntityValueType(parent.getDataSpec().getDataType());
            if (parentDataType == null) {
                return;
            }
            val parentEntity = new EntityBuilder(integrationId, deviceKey)
                    .identifier(parent.getId())
                    .property(parent.getName(), AccessMod.valueOf(parent.getAccessMode().name()))
                    .valueType(parentDataType)
                    .attributes(convertTslDataSpecToEntityAttributes(parent.getDataSpec()))
                    .build();
            entities.add(parentEntity);

            if (children.isEmpty()) {
                return;
            }
            val existsIdSet = specs.stream()
                    .map(TslPropertySpec::getId)
                    .collect(Collectors.toSet());
            parentEntity.setChildren(children.stream()
                    // make sure parent is handled before children
                    .sorted(Comparator.comparing(TslPropertySpec::getId))
                    .map(child -> {
                        val id = child.getId();
                        val dataSpec = child.getDataSpec();
                        val name = child.getName();
                        val valueType = checkParentExistsAndGetValueType(id, dataSpec, existsIdSet);
                        if (valueType == null) {
                            return null;
                        }
                        if (!existsIdSet.contains(id)) {
                            return null;
                        }
                        val accessMod = AccessMod.valueOf(child.getAccessMode().name());
                        return new EntityBuilder(integrationId, deviceKey)
                                .identifier(standardizeChildEntityIdentifier(id))
                                .property(name, accessMod)
                                .valueType(valueType)
                                .attributes(convertTslDataSpecToEntityAttributes(dataSpec))
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .toList());
        });
        return entities;
    }

    static List<Entity> getEventEntities(String integrationId, String deviceKey, ThingSpec thingSpec) {
        val entities = new ArrayList<Entity>();
        val events = thingSpec.getEvents();
        if (events == null || events.isEmpty()) {
            return entities;
        }
        val idToEvent = events.stream()
                .collect(Collectors.toMap(TslEventSpec::getId, Function.identity()));
        val eventParamsGroup = events.stream()
                .filter(spec -> spec.getOutputs() != null && !spec.getOutputs().isEmpty())
                .collect(Collectors.toMap(TslEventSpec::getId, TslEventSpec::getOutputs));

        //noinspection DuplicatedCode
        idToEvent.forEach((key, eventSpec) -> {
            val parentEntity = new EntityBuilder(integrationId, deviceKey)
                    .identifier(eventSpec.getId())
                    .event(eventSpec.getName())
                    .valueType(EntityValueType.OBJECT)
                    .build();
            entities.add(parentEntity);

            val params = eventParamsGroup.get(key);
            if (params == null) {
                parentEntity.setValueType(EntityValueType.BOOLEAN);
                return;
            }
            val existsIdSet = params.stream()
                    .map(TslParamSpec::getId)
                    .collect(Collectors.toSet());
            existsIdSet.add(key);
            parentEntity.setChildren(params.stream()
                    // ref without id is not supported yet
                    .filter(param -> param.getId() != null && param.getDataSpec() != null)
                    // make sure parent is handled before children
                    .sorted(Comparator.comparing(TslParamSpec::getId))
                    .map(param -> {
                        val id = param.getId();
                        val dataSpec = param.getDataSpec();
                        val name = param.getName();
                        val valueType = checkParentExistsAndGetValueType(id, dataSpec, existsIdSet);
                        if (valueType == null) {
                            return null;
                        }
                        if (!existsIdSet.contains(id)) {
                            return null;
                        }
                        return new EntityBuilder(integrationId, deviceKey)
                                .identifier(standardizeChildEntityIdentifier(id))
                                .event(name)
                                .valueType(valueType)
                                .attributes(convertTslDataSpecToEntityAttributes(dataSpec))
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .toList());
        });
        return entities;
    }

    static List<Entity> getServiceEntities(String integrationId, String deviceKey, ThingSpec thingSpec) {
        val entities = new ArrayList<Entity>();
        val services = thingSpec.getServices();
        if (services == null || services.isEmpty()) {
            return entities;
        }
        val idToService = services.stream()
                .collect(Collectors.toMap(TslServiceSpec::getId, Function.identity()));
        val serviceParamsGroup = services.stream()
                .filter(spec -> spec.getInputs() != null && !spec.getInputs().isEmpty())
                .collect(Collectors.toMap(TslServiceSpec::getId, TslServiceSpec::getInputs));

        //noinspection DuplicatedCode
        idToService.forEach((key, serviceSpec) -> {
            val parentEntity = new EntityBuilder(integrationId, deviceKey)
                    .identifier(serviceSpec.getId())
                    .service(serviceSpec.getName())
                    .valueType(EntityValueType.OBJECT)
                    .build();
            entities.add(parentEntity);

            val params = serviceParamsGroup.get(key);
            if (params == null) {
                parentEntity.setValueType(EntityValueType.BOOLEAN);
                return;
            }
            val existsIdSet = params.stream()
                    .map(TslParamSpec::getId)
                    .collect(Collectors.toSet());
            existsIdSet.add(key);
            parentEntity.setChildren(params.stream()
                    // ref without id is not supported yet
                    .filter(param -> param.getId() != null && param.getDataSpec() != null)
                    // make sure parent is handled before children
                    .sorted(Comparator.comparing(TslParamSpec::getId))
                    .map(param -> {
                        val id = param.getId();
                        val dataSpec = param.getDataSpec();
                        val name = param.getName();
                        val valueType = checkParentExistsAndGetValueType(id, dataSpec, existsIdSet);
                        if (valueType == null) {
                            return null;
                        }
                        if (!existsIdSet.contains(id)) {
                            return null;
                        }
                        return new EntityBuilder(integrationId, deviceKey)
                                .identifier(standardizeChildEntityIdentifier(id))
                                .service(name)
                                .valueType(valueType)
                                .attributes(convertTslDataSpecToEntityAttributes(dataSpec))
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .toList());
        });
        return entities;
    }

    @SuppressWarnings({"java:S3516"})
    private static EntityValueType checkParentExistsAndGetValueType(String id, TslDataSpec dataSpec, Set<String> existsIdSet) {
        if (!existsIdSet.contains(id) || !existsIdSet.contains(dataSpec.getParentId())) {
            return null;
        }
        val valueType = convertDataTypeToEntityValueType(dataSpec.getDataType());
        if (valueType == null) {
            // remove invalid item
            existsIdSet.remove(id);
        }
        return valueType;
    }

    private static String getRootParentId(String id, String parentId) {
        if (parentId != null) {
            val ids = parentId.split("\\.");
            if (ids.length > 0) {
                return ids[0];
            }
        }
        return id;
    }

    private static String standardizeChildEntityIdentifier(String rawIdentifier) {
        val fullIdentifier = rawIdentifier.replace('.', '@');
        return fullIdentifier.substring(fullIdentifier.indexOf('@') + 1);
    }

    private static Map<String, Object> convertTslDataSpecToEntityAttributes(TslDataSpec dataSpec) {
        val attributeBuilder = new AttributeBuilder();
        if (dataSpec.getUnitName() != null) {
            attributeBuilder.unit(dataSpec.getUnitName());
        }
        if (dataSpec.getValidator() != null) {
            if (dataSpec.getValidator().getMax() != null) {
                attributeBuilder.max(dataSpec.getValidator().getMax().doubleValue());
            }
            if (dataSpec.getValidator().getMin() != null) {
                attributeBuilder.min(dataSpec.getValidator().getMin().doubleValue());
            }
            if (dataSpec.getValidator().getMaxSize() != null) {
                attributeBuilder.maxLength(dataSpec.getValidator().getMaxSize().intValue());
            }
            if (dataSpec.getValidator().getMinSize() != null) {
                attributeBuilder.minLength(dataSpec.getValidator().getMinSize().intValue());
            }
        }
        if (dataSpec.getFractionDigits() != null) {
            attributeBuilder.fractionDigits(dataSpec.getFractionDigits().intValue());
        }
        if (dataSpec.getMappings() != null && !dataSpec.getMappings().isEmpty()) {
            attributeBuilder.enums(dataSpec.getMappings()
                    .stream()
                    .collect(Collectors.toMap(TslKeyValuePair::getKey, TslKeyValuePair::getValue, (a, b) -> a)));
        }
        return attributeBuilder.build();
    }

    private static EntityValueType convertDataTypeToEntityValueType(TslDataSpec.DataTypeEnum dataType) {
        switch (dataType) {
            case STRING, ENUM, FILE, IMAGE:
                return EntityValueType.STRING;
            case INT, LONG, DATE, LOCAL_TIME:
                return EntityValueType.LONG;
            case FLOAT, DOUBLE:
                return EntityValueType.DOUBLE;
            case BOOL:
                return EntityValueType.BOOLEAN;
            case STRUCT:
                return EntityValueType.OBJECT;
            case ARRAY:
                // todo handle array
            default:
                log.warn("Unsupported data type: {}", dataType);
                return null;
        }
    }

    @Nullable
    public static ExchangePayload convertJsonNodeToExchangePayload(String deviceKey, JsonNode jsonNode) {
        return convertJsonNodeToExchangePayload(deviceKey, jsonNode, true);
    }

    /**
     * Convert json node to exchange payload
     *
     * @param previousEntityKey  The entity key from parent entity
     * @param jsonNode           The json data
     * @param isRoot             If json data is root properties, it should be true, otherwise it should be false
     * @return exchange payload
     */
    @Nullable
    public static ExchangePayload convertJsonNodeToExchangePayload(String previousEntityKey, JsonNode jsonNode, boolean isRoot) {
        val result = new HashMap<String, Object>();
        if (jsonNode == null || jsonNode.isEmpty() || !jsonNode.isObject()) {
            return null;
        }
        val entries = new ArrayDeque<JsonEntry>();
        entries.push(new JsonEntry(previousEntityKey, jsonNode));
        int level = isRoot ? 0 : 1;
        while (!entries.isEmpty()) {
            val parent = entries.pop();
            val currentLevel = level;
            parent.value.fields().forEachRemaining(entry -> {
                val fieldName = entry.getKey();
                val value = entry.getValue();
                val parentEntityKey = parent.parentEntityKey;
                val entityKeyTemplate = currentLevel < 1 ? "%s.%s" : "%s@%s";
                val entityKey = String.format(entityKeyTemplate, parentEntityKey, fieldName);
                if (value == null || value.isNull()) {
                    log.debug("Null value is ignored: {}", entityKey);
                } else if (value.isArray()) {
                    log.debug("Array is not supported yet: {}", entityKey);
                    // todo support array
                } else if (value.isObject()) {
                    entries.push(new JsonEntry(entityKey, value));
                } else {
                    result.put(entityKey, value);
                }
            });
            level++;
        }
        if (result.isEmpty()) {
            return null;
        }
        return ExchangePayload.create(result);
    }

    private record JsonEntry(String parentEntityKey, JsonNode value) {
    }

    public static Map<String, JsonNode> convertExchangePayloadMapToGroupedJsonNode(@NotNull ObjectMapper objectMapper, @NotNull String entityKeyPublicPrefix, @NotNull Map<String, Object> keyValues) {
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(entityKeyPublicPrefix);
        Objects.requireNonNull(keyValues);

        val result = new HashMap<String, JsonNode>();
        keyValues.forEach((key, value) -> {
            if (!key.startsWith(entityKeyPublicPrefix) || key.equals(entityKeyPublicPrefix)) {
                log.debug("Ignored invalid key: {}, prefix is {}", key, entityKeyPublicPrefix);
                return;
            }
            val paths  = key.substring(entityKeyPublicPrefix.length() + 1).split("[.|@]");
            if (paths.length == 0) {
                log.debug("Ignored invalid key: {}", key);
                return;
            }
            if (value == null) {
                log.debug("Null value is ignored: {}", key);
                return;
            }
            if (paths.length == 1) {
                result.computeIfAbsent(paths[0], k -> objectMapper.convertValue(value, JsonNode.class));
            } else {
                // todo support array
                ensureParentAndSetValue(objectMapper, result, key, value, paths);
            }
        });
        return result;
    }

    private static void ensureParentAndSetValue(@NotNull ObjectMapper objectMapper, HashMap<String, JsonNode> result, String key, Object value, String[] paths) {
        var parent = result.computeIfAbsent(paths[0], k -> objectMapper.createObjectNode());
        val lastIndex = paths.length - 1;
        for (int i = 1; i < lastIndex; i++) {
            var field = parent.get(paths[i]);
            if (field == null || !field.isObject()) {
                if (parent instanceof ObjectNode p) {
                    field = objectMapper.createObjectNode();
                    p.set(paths[i], field);
                    parent = field;
                } else {
                    log.debug("Invalid parent node: {} {} {}", key, i, parent);
                    return;
                }
            }
        }
        if (parent instanceof ObjectNode p) {
            p.set(paths[lastIndex], objectMapper.convertValue(value, JsonNode.class));
        }
    }

}
