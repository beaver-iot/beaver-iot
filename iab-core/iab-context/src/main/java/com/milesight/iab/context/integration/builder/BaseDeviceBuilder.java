package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.util.Assert;

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
    protected Integration integration;
    protected Long id;

    public BaseDeviceBuilder(Integration integration){
        this.integration = integration;
    }

    public BaseDeviceBuilder(String integrationId, String integrationName){
        this.integration = Integration.of(integrationId, integrationName);
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
        device.setEntities(entities);
        if(integration != null){
            device.setIntegrationId(integration.getId());
            device.initializeProperties(integration.getId());
        }
        device.setId(id);
        return device;
    }

}
