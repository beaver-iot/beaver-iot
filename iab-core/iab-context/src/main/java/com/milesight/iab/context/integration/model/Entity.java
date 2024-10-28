package com.milesight.iab.context.integration.model;

import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.constants.IntegrationConstants;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.context.support.SpringContext;
import com.milesight.iab.eventbus.api.IdentityKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author leon
 */
@Getter
@Setter
public class Entity implements IdentityKey {

    private Long id;
    private String deviceKey;
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

    public String getParentKey() {
        if(!StringUtils.hasLength(parentIdentifier)){
            return null;
        }
        if(StringUtils.hasText(deviceKey)){
            return IntegrationConstants.formatIntegrationDeviceEntityKey(deviceKey, parentIdentifier);
        }else{
            return IntegrationConstants.formatIntegrationEntityKey(integrationId, parentIdentifier);
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

    public Optional<Device> loadDevice(){
        DeviceServiceProvider deviceServiceProvider = SpringContext.getBean(DeviceServiceProvider.class);
        return StringUtils.hasText(deviceKey) ? Optional.of(deviceServiceProvider.findByKey(deviceKey)) : Optional.empty();
    }

    public Optional<Integration> loadActiveIntegration(){
        IntegrationServiceProvider integrationServiceProvider = SpringContext.getBean(IntegrationServiceProvider.class);
        return Optional.ofNullable(integrationServiceProvider.getActiveIntegration(integrationId));
    }

}
