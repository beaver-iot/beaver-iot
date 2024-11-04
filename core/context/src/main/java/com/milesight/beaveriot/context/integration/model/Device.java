package com.milesight.beaveriot.context.integration.model;

import com.milesight.beaveriot.context.constants.IntegrationConstants;
import com.milesight.beaveriot.eventbus.api.IdentityKey;
import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Getter
public class Device implements IdentityKey {

    private Long id;
    private String integrationId;
    private String name;
    private Map<String,Object> additional;
    private String identifier;
    private List<Entity> entities = new ArrayList<>();

    protected Device() {
    }
    protected Device(String name, Map<String,Object> additional, String identifier, List<Entity> entityConfigs) {
        this.name = name;
        this.additional = additional;
        this.entities = entityConfigs;
        this.identifier = identifier;
    }

    @Override
    public String getKey() {
        return IntegrationConstants.formatIntegrationDeviceKey(integrationId, identifier);
    }

    protected void initializeProperties(String integrationId) {
        if(integrationId == null){
            return;
        }
        validate();
        this.setIntegrationId(integrationId);
        List<Entity> entitiesList = getEntities();
        if(!CollectionUtils.isEmpty(entitiesList)){
            for (Entity entity : entitiesList) {
                entity.initializeProperties(integrationId, getKey());
            }
        }
    }

    public void validate() {
        Assert.notNull(name, "Device name must not be null");
        Assert.notNull(identifier, "Device identifier must not be null");
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdditional(Map<String, Object> additional) {
        this.additional = additional;
    }

    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setEntities(List<Entity> entities) {
        Assert.notNull(integrationId, "Integration must not be null");
        initializeProperties(integrationId);
        this.entities = entities;
    }
}
