package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.enums.AttachTargetType;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public interface EntityServiceProvider {

    List<Entity> findByTargetId(AttachTargetType targetType, String targetId);

    List<Entity> findByTargetIds(AttachTargetType targetType, List<String> targetIds);

    void save(Entity entity);

    void batchSave(List<Entity> entityList);

    void deleteByTargetId(String targetId);

    long countAllEntitiesByIntegrationId(String integrationId);

    Map<String, Long> countAllEntitiesByIntegrationIds(List<String> integrationIds);

    long countIntegrationEntitiesByIntegrationId(String integrationId);

    Map<String, Long> countIntegrationEntitiesByIntegrationIds(List<String> integrationIds);

    void saveExchange(ExchangePayload exchangePayload);

    void saveExchangeHistory(ExchangePayload exchangePayload);

    Object findExchangeValueByKey(String key);

    <T> T findExchangeByKey(String key, Class<T> entitiesClazz);

    Entity findByKey(String entityKey);

    Map<String, Entity> findByKeys(String... entityKeys);
}
