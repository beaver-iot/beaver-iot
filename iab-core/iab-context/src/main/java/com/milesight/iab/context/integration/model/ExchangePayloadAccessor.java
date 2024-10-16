package com.milesight.iab.context.integration.model;


import java.util.Map;

/**
 * todo: remove？
 * @author leon
 */
public interface ExchangePayloadAccessor {

    Object getPayload(String key);

    Map<String,Object> getAllPayloads();

}
