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

    void deleteByTargetId(String targetId);

    long countAllEntitiesByIntegration(String integrationId);

    long countIntegrationEntitiesByIntegration(String integrationId);

    void saveExchange(ExchangePayload exchangePayload);

    void saveExchangeHistory(ExchangePayload exchangePayload);

    Object findExchangeValueByKey(String key);

    <T> T findExchangeByKey(String key, Class<T> entitiesClazz);

}
