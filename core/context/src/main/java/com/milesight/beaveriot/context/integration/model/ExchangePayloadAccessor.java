package com.milesight.beaveriot.context.integration.model;


import com.milesight.beaveriot.context.integration.enums.EntityType;

import java.util.Map;

/**
 * todo: remove？
 * @author leon
 */
public interface ExchangePayloadAccessor {

    Object getPayload(String key);

    Map<String,Object> getAllPayloads();

    Map<String,Object> getPayloadsByEntityType(EntityType entityType);

}