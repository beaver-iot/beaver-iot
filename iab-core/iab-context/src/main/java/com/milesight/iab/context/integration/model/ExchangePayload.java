package com.milesight.iab.context.integration.model;


import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.support.SpringContext;
import com.milesight.iab.eventbus.api.IdentityKey;
import lombok.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.milesight.iab.context.constants.ExchangeContextKeys.*;

/**
 * @author leon
 */
public class ExchangePayload extends HashMap<String,Object> implements ExchangePayloadAccessor, EventContextAccessor, IdentityKey {

    private transient Map<String,Object> context = new HashMap<>();

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
        if(context == null){
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

    public static <T extends ExchangePayloadAccessor> ExchangePayload createFrom(T payload, List<String> assignKeys){
        if(ObjectUtils.isEmpty(payload) || ObjectUtils.isEmpty(assignKeys)){
            return new ExchangePayload(payload.getAllPayloads());
        }

        Map<String, Object> filteredMap = payload.getAllPayloads().entrySet()
                .stream()
                .filter(entry -> CollectionUtils.contains(assignKeys.iterator(), entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ExchangePayload(filteredMap);
    }

    public Map<String,Entity> getExchangeEntities() {
        if(ObjectUtils.isEmpty(keySet())){
            return Map.of();
        }

        Map<String,Entity> entityMap = (Map<String, Entity>) getContext(EXCHANGE_KEY_ENTITIES);
        if(ObjectUtils.isEmpty(entityMap)){
            EntityServiceProvider entityServiceProvider = SpringContext.getBean(EntityServiceProvider.class);
            entityMap = entityServiceProvider.findByKeys(keySet().toArray(String[]::new));
        }
        return entityMap;
    }

    @Override
    public Map<String,Object> getPayloadsByEntityType(EntityType entityType) {
        if(ObjectUtils.isEmpty(keySet())){
            return Map.of();
        }

        Map<String, Entity> exchangeEntities = getExchangeEntities();
        return entrySet().stream().filter(entry -> {
            Entity entity = exchangeEntities.get(entry.getKey());
            return entity != null && entity.getType() == entityType;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
