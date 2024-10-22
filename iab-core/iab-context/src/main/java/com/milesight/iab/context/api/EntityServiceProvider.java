package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;

import java.util.List;

/**
 * @author leon
 */
public interface EntityServiceProvider {

    List<Entity> findByTargetId(String targetId);

    void save(Entity entity);

    void batchSave(List<Entity> entityList);

    void saveExchange(ExchangePayload exchangePayloadList);

    void saveExchangeHistory(ExchangePayload exchangePayloadList);

    Entity findByIdentifier(String identifier);

    List<Entity> findAllByIntegration(String integration);

    <T> T findExchangeByKey(String key, Class<T> entitiesClazz);

}
