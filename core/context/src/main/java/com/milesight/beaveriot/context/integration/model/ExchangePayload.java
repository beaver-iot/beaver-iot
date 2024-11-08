package com.milesight.beaveriot.context.integration.model;


import com.milesight.beaveriot.context.api.EntityServiceProvider;
import com.milesight.beaveriot.context.constants.ExchangeContextKeys;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.support.SpringContext;
import com.milesight.beaveriot.eventbus.api.IdentityKey;
import lombok.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class ExchangePayload extends HashMap<String, Object> implements ExchangePayloadAccessor, EventContextAccessor, IdentityKey {

    private transient Map<String, Object> context = new HashMap<>();

    private long timestamp = System.currentTimeMillis();

    public ExchangePayload() {
    }

    public ExchangePayload(Map<String, Object> payloads) {
        this.putAll(payloads);
    }

    @Override
    public String getKey() {
        return keySet().stream().collect(Collectors.joining(","));
    }

    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public Object getContext(String key) {
        if (context == null) {
            return null;
        }
        return context.get(key);
    }

    @Override
    public <T> T getContext(String key, T defaultValue) {
        Object value = getContext(key);
        return value == null ? defaultValue : (T) value;
    }

    @Override
    public void putContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Object getPayload(String key) {
        return this.get(key);
    }

    @Override
    public Map<String, Object> getAllPayloads() {
        return this;
    }


    public static ExchangePayload empty() {
        return new ExchangePayload();
    }

    public static ExchangePayload create(String key, Object value) {
        ExchangePayload exchangePayload = new ExchangePayload();
        exchangePayload.put(key, value);
        return exchangePayload;
    }

    public static ExchangePayload create(Map<String, Object> values) {
        return new ExchangePayload(values);
    }

    public static ExchangePayload create(Map<String, Object> values, Map<String, Object> context) {
        ExchangePayload exchangePayload = new ExchangePayload(values);
        exchangePayload.setContext(context);
        return exchangePayload;
    }

    public static <T extends ExchangePayload> ExchangePayload createFrom(T payload) {
        return create(payload.getAllPayloads(), payload.getContext());
    }

    public static <T extends ExchangePayload> ExchangePayload createFrom(T payload, List<String> assignKeys) {
        if (ObjectUtils.isEmpty(payload) || ObjectUtils.isEmpty(assignKeys)) {
            return ExchangePayload.create(payload.getAllPayloads(), payload.getContext());
        }

        Map<String, Object> filteredPayload = new LinkedHashMap<>();
        payload.getAllPayloads().forEach((key, value) -> {
            if(CollectionUtils.contains(assignKeys.iterator(), key)){
                filteredPayload.put(key, value);
            }
        });
        return ExchangePayload.create(filteredPayload, payload.getContext());
    }

    public Map<String, Entity> getExchangeEntities() {
        if (ObjectUtils.isEmpty(keySet())) {
            return Map.of();
        }

        Map<String, Entity> entityMap = (Map<String, Entity>) getContext(ExchangeContextKeys.ENTITIES);
        if (ObjectUtils.isEmpty(entityMap)) {
            EntityServiceProvider entityServiceProvider = SpringContext.getBean(EntityServiceProvider.class);
            entityMap = entityServiceProvider.findByKeys(keySet().toArray(String[]::new));
        }
        return entityMap;
    }

    @Override
    @NonNull
    public Map<String, Object> getPayloadsByEntityType(EntityType entityType) {
        if (ObjectUtils.isEmpty(keySet())) {
            return Map.of();
        }

        Map<String,Object> payloads = new HashMap<>();
        Map<String, Entity> exchangeEntities = getExchangeEntities();
        this.forEach((key, value) -> {
            Entity entity = exchangeEntities.get(key);
            if(entity != null && entity.getType() == entityType){
                payloads.put(key, value);
            }
        });
        return payloads;
    }

}
