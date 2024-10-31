package com.milesight.beaveriot.context.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public interface EntityValueServiceProvider {

    void saveValues(Map<String,Object> values, long timestamp);

    void saveValues(Map<String,Object> values);

    void saveHistoryRecord(Map<String,Object> recordValues, long timestamp);

    void saveHistoryRecord(Map<String,Object> recordValues);

    JsonNode findValueByKey(String key);

    Map<String, JsonNode> findValuesByKeys(List<String> keys);

    <T> T findValuesByKey(String key, Class<T> entitiesClazz);

}
