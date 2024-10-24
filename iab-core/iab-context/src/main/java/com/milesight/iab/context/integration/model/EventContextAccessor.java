package com.milesight.iab.context.integration.model;


import java.util.Map;

/**
 * todo: remove？
 * @author leon
 */
public interface EventContextAccessor {

    static final String EXCHANGE_KEY_ENTITIES = "KEY_ENTITIES";

    static final String EXCHANGE_KEY_SYNC_CALL = "KEY_SYNC_CALL";

    static final String EXCHANGE_KEY_EVENT_TYPE = "KEY_EVENT_TYPE";

    Map<String, Object> getContext();

    void setContext(Map<String, Object> context);

    Object getContext(String key);

    <T> T getContext(String key, T defaultValue);

    void putContext(String key, Object value);
}
