package com.milesight.iab.entity.service;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.entity.model.request.CallServiceRequest;
import com.milesight.iab.entity.model.request.EntityHistoryQuery;
import com.milesight.iab.entity.model.request.EntityQuery;
import com.milesight.iab.entity.model.request.UpdateEntityRequest;
import com.milesight.iab.entity.repository.EntityHistoryRepository;
import com.milesight.iab.entity.repository.EntityLatestRepository;
import com.milesight.iab.entity.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/16 14:22
 */
@Service
public class EntityService implements EntityServiceProvider {

    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private EntityHistoryRepository entityHistoryRepository;
    @Autowired
    private EntityLatestRepository entityLatestRepository;

    @Override
    public void save(Entity entity) {
        //TODO
    }

    @Override
    public void batchSave(List<Entity> entityList) {
        //TODO
    }

    @Override
    public void saveExchange(ExchangePayload exchangePayloadList, boolean includedHistory) {
        //TODO
    }

    @Override
    public Entity findByIdentifier(String identifier) {
        //TODO
        return null;
    }

    @Override
    public List<Entity> findAllByIntegration(String integration) {
        //TODO
        return null;
    }

    @Override
    public <T> T findByKey(String key, Class<T> entitiesClazz) {
        //TODO
        return null;
    }

    public ResponseBody search(EntityQuery entityQuery) {
        //TODO
        return null;
    }

    public String historySearch(EntityHistoryQuery entityHistoryQuery) {
        //TODO
        return null;
    }

    public String getEntityStatus(Long entityId) {
        //TODO
        return null;
    }

    public String updateEntity(UpdateEntityRequest updateEntityRequest) {
        //TODO
        return null;
    }

    public String callService(CallServiceRequest callServiceRequest) {
        //TODO
        return null;
    }
}
