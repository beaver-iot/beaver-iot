package com.milesight.iab.base.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.milesight.iab.base.exception.JSONException;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * json util
 * @author  leon
 */
public class JsonUtils {
    private static final ObjectMapper JSON = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    public static ObjectMapper getObjectMapper() {
        return JSON;
    }

    public static <T> T copy(T object) {
        return cast(object, getClass(object));
    }

    public static <T> T copy(T object, TypeReference<T> typeReference) {
        return cast(object, typeReference);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(T object) {
        return (Class<T>) object.getClass();
    }

    public static <T> T cast(Object object, TypeReference<T> typeReference) {
        if (object == null) {
            return null;
        }
        return JSON.convertValue(object, typeReference);
    }

    public static <T> T cast(Object object, Class<T> classType) {
        if (object == null) {
            return null;
        }
        return JSON.convertValue(object, classType);
    }

    public static String toPrettyJSON(String json) {
        return toPrettyJSON(fromJSON(json));
    }

    public static String toJSON(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    public static String toPrettyJSON(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    public static Map<String, Object> toMap(String json) {
        return fromJsonWithType(json);
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object object) {
        return cast(object, Map.class);
    }

    public static <T> T fromJSON(String json, Class<T> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    public static <T> T fromJSON(String json, TypeReference<T> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    public static <T> T fromJsonWithType(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.readValue(json, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    public static JsonNode fromJSON(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    public static JsonNode toJsonNode(Object object) {
        if (object instanceof String) {
            return fromJSON((String) object);
        }
        return cast(object, JsonNode.class);
    }

    public static boolean equals(Object source, Object target) {
        return Objects.equals(toJsonNode(source), toJsonNode(target));
    }



}