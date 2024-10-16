package com.milesight.iab.context.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milesight.iab.context.constants.IntegrationConstants;
import com.milesight.iab.eventbus.api.IdentityKey;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Getter
@Setter
public class Device implements IdentityKey {

    @JsonIgnore
    private Integration integration;
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
        return IntegrationConstants.formatIntegrationDeviceKey(integration.getName(), identifier);
    }
}
