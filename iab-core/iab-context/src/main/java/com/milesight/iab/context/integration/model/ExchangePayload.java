package com.milesight.iab.context.integration.model;


import com.milesight.iab.eventbus.api.IdentityKey;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
public class ExchangePayload extends HashMap<String,Object> implements ExchangePayloadAccessor, IdentityKey {

    private transient Map<String,Object> context;

    public ExchangePayload() {
    }

    public ExchangePayload(Map<String, Object> payloads) {
        this.putAll(payloads);
    }

    @Override
    public String getKey() {
        if(this.size() == 1){
            return this.keySet().iterator().next();
        }else {
            //todo： group key
            return null;
        }
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
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

}
