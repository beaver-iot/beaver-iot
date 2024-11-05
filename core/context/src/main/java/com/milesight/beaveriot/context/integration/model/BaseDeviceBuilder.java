package com.milesight.beaveriot.context.integration.model;

import com.milesight.beaveriot.context.support.IdentifierValidator;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class BaseDeviceBuilder<T extends BaseDeviceBuilder> {

    protected List<Entity> entities;
    protected String name;
    protected String identifier;
    protected Map<String,Object> additional;
    protected String integrationId;
    protected Long id;

    public BaseDeviceBuilder(String integrationId){
        this.integrationId = integrationId;
    }

    public BaseDeviceBuilder(){
    }

    public T entity(Entity entity) {
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.add(entity);
        return (T) this;
    }

    public T entities(List<Entity> addEntities) {
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.addAll(addEntities);
        return (T) this;
    }

    public T id(Long id) {
        this.id = id;
        return (T) this;
    }

    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public T identifier(String identifier) {
        IdentifierValidator.isValid(identifier);
        this.identifier = identifier;
        return (T) this;
    }

    public T additional(Map<String, Object> additional) {
        this.additional = additional;
        return (T) this;
    }

    public Device build(){

        Device device = new Device();
        device.setName(name);
        device.setAdditional(additional);
        device.setIdentifier(identifier);
        if(StringUtils.hasText(integrationId)){
            device.setIntegrationId(integrationId);
            device.initializeProperties(integrationId);
        }
        device.setId(id);
        device.setEntities(entities);
        return device;
    }

}
