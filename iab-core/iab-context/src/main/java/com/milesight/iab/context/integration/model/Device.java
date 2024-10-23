package com.milesight.iab.context.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milesight.iab.context.constants.IntegrationConstants;
import com.milesight.iab.eventbus.api.IdentityKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Getter
@Setter
public class Device implements IdentityKey {

    private Long id;
    private String integrationId;
    private String name;
    private Map<String,Object> additional;
    private String identifier;
    private List<Entity> entities = new ArrayList<>();

    public Device() {
    }
    public Device(String name, Map<String,Object> additional, String identifier, List<Entity> entityConfigs) {
        this.name = name;
        this.additional = additional;
        this.entities = entityConfigs;
        this.identifier = identifier;
    }

    @Override
    public String getKey() {
        return IntegrationConstants.formatIntegrationDeviceKey(integrationId, identifier);
    }

    public void initializeProperties(String integrationId) {
        if(integrationId == null){
            return;
        }
        this.setIntegrationId(integrationId);
        List<Entity> entitiesList = getEntities();
        if(!CollectionUtils.isEmpty(entitiesList)){
            for (Entity entity : entitiesList) {
                entity.initializeProperties(integrationId, getKey());
            }
        }
    }
}
