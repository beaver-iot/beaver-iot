package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;

import java.util.List;

/**
 * @author leon
 */
public interface EntityServiceProvider {

    void save(Entity entity);

    void batchSave(List<Entity> entityList);

    void saveExchange(ExchangePayload exchangePayloadList, boolean includedHistory);

}
