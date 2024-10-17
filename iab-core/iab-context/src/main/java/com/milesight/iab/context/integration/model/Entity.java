package com.milesight.iab.context.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milesight.iab.context.constants.IntegrationConstants;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.eventbus.api.IdentityKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Getter
@Setter
public class Entity implements IdentityKey {

    @JsonIgnore
    private Device device;
    @JsonIgnore
    private Integration integration;
    private String name;
    private String identifier;
    private AccessMod accessMod;
    private boolean syncCall = true;
    private EntityValueType valueType;
    private EntityType type;
    private Map<String,Object> attributes;
    private String group;
    private List<Entity> children;

    @Override
    public String getKey() {
        String fullIdentifier = StringUtils.hasLength(group) ? group + "." + identifier : identifier;
        if(device != null){
            return IntegrationConstants.formatIntegrationDeviceEntityKey(integration.getName(), device.getIdentifier(), fullIdentifier);
        }else{
            return IntegrationConstants.formatIntegrationEntityKey(integration.getName(), fullIdentifier);
        }
    }

    public void initializeProperties(Integration integration, Device device){
        Assert.notNull(integration, "Integration must not be null");
        Assert.notNull(device, "Device must not be null");
        this.setIntegration(integration);
        this.setDevice(device);
        if(!CollectionUtils.isEmpty(children)){
            children.forEach(entity -> {
                entity.setDevice(device);
                entity.setIntegration(integration);
            });
        }
    }

    public void initializeProperties(Integration integration){
        Assert.notNull(integration, "Integration must not be null");
        this.setIntegration(integration);
        if(!CollectionUtils.isEmpty(children)){
            children.forEach(entity -> {
                entity.setIntegration(integration);
            });
        }
    }

}
