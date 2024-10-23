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

    private Long id;
    @JsonIgnore
    private String deviceKey;
    @JsonIgnore
    private String integrationId;
    private String name;
    private String identifier;
    private AccessMod accessMod;
    private boolean syncCall = true;
    private EntityValueType valueType;
    private EntityType type;
    private Map<String,Object> attributes;
    private String parentIdentifier;
    private List<Entity> children;

    public String getFullIdentifier(){
        return StringUtils.hasLength(parentIdentifier) ? parentIdentifier + "." + identifier : identifier;
    }

    @Override
    public String getKey() {
        String fullIdentifier = getFullIdentifier();
        if(StringUtils.hasText(deviceKey)){
            return IntegrationConstants.formatIntegrationDeviceEntityKey(deviceKey, fullIdentifier);
        }else{
            return IntegrationConstants.formatIntegrationEntityKey(integrationId, fullIdentifier);
        }
    }

    public void initializeProperties(String integrationId, String deviceKey){
        Assert.notNull(integrationId, "Integration must not be null");
        Assert.notNull(deviceKey, "Device must not be null");
        this.setIntegrationId(integrationId);
        this.setDeviceKey(deviceKey);
        if(!CollectionUtils.isEmpty(children)){
            children.forEach(entity -> {
                entity.setDeviceKey(deviceKey);
                entity.setIntegrationId(integrationId);
            });
        }
    }

    public void initializeProperties(String integrationId){
        Assert.notNull(integrationId, "Integration must not be null");
        this.setIntegrationId(integrationId);
        if(!CollectionUtils.isEmpty(children)){
            children.forEach(entity -> {
                entity.setIntegrationId(integrationId);
            });
        }
    }

}
