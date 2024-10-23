package com.milesight.iab.integration.msc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.cloud.sdk.client.model.ThingSpec;
import com.milesight.iab.context.integration.model.Entity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TslUtils {

    private TslUtils() {
        throw new IllegalStateException("Utility class");
    }

    @Nonnull
    public static List<Entity> tslToEntities(ThingSpec thingSpec) {
        // todo
        return new ArrayList<>();
    }

    @Nonnull
    public static Map<String, Map<String, JsonNode>> jsonNodeToKeyValues(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isEmpty() || jsonNode.isArray()) {
            return new HashMap<>();
        }
        // todo
        return new HashMap<>();
    }

    public static JsonNode exchangePayloadMapToJsonNode(ObjectMapper objectMapper, Map<String, Object> keyValues) {
        // todo
        return objectMapper.createObjectNode();
    }

    public static Map<String, JsonNode> exchangePayloadMapToGroupedJsonNode(ObjectMapper objectMapper, Map<String, Object> keyValues) {
        // todo
        return new HashMap<>();
    }

}
