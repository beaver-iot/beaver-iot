package com.milesight.iab.context.integration.model;


import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.eventbus.api.IdentityKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class ExchangePayload extends HashMap<String,Object> implements ExchangePayloadAccessor, IdentityKey {

    private transient Map<String,Object> context;

    private long timestamp;

    public ExchangePayload() {
        this.timestamp = System.currentTimeMillis();
    }

    public ExchangePayload(Map<String, Object> payloads) {
        this.putAll(payloads);
    }

    @Override
    public String getKey() {
        return keySet().stream().collect(Collectors.joining(","));
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Object getContext(String key) {
        if(context == null){
           return null;
        }
        return context.get(key);
    }


    public void putContext(String key, Object value) {
        if(context == null){
            context = new HashMap<>();
        }
        context.put(key,value);
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

    public static ExchangePayload create(String key, Object value) {
        ExchangePayload exchangePayload = new ExchangePayload();
        exchangePayload.put(key, value);
        return exchangePayload;
    }

    public static ExchangePayload create(Map<String,Object> values) {
        return new ExchangePayload(values);
    }

    public static <T extends ExchangePayloadAccessor> ExchangePayload createFrom(T payload){
        return new ExchangePayload(payload.getAllPayloads());
    }

    public static <T extends ExchangePayloadAccessor> ExchangePayload createFrom(T payload, List<String> keys){
        if(ObjectUtils.isEmpty(payload) || ObjectUtils.isEmpty(keys)){
            return new ExchangePayload(payload.getAllPayloads());
        }

        Map<String, Object> filteredMap = payload.getAllPayloads().entrySet()
                .stream()
                .filter(entry -> CollectionUtils.contains(keys.iterator(), entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ExchangePayload(filteredMap);
    }

    @NonNull
    public List<Entity> getEntities() {
        return new ArrayList<>();
    }

    @NonNull
    public ExchangePayload getPayloadsByEntityType(EntityType entityType) {
        return null;
    }

}
