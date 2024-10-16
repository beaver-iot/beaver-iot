package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class DeviceBuilder {
    private List<Entity> entities;
    private String name;
    private String identifier;
    private Map<String,Object> additional;
    private IntegrationBuilder integrationBuilder;
    public DeviceBuilder(IntegrationBuilder integrationBuilder) {
        this.integrationBuilder = integrationBuilder;
    }

    public DeviceBuilder(){
    }

    public DeviceBuilder entity(Entity entity) {
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.add(entity);
        return this;
    }

    public DeviceBuilder entities(List<Entity> addEntities) {
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.addAll(addEntities);
        return this;
    }

    public IntegrationBuilder end() {
        integrationBuilder.integration.addDevice(build());
        return integrationBuilder;
    }

    public DeviceBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DeviceBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public DeviceBuilder additional(Map<String, Object> additional) {
        this.additional = additional;
        return this;
    }

    public Device build(){
        Device device = new Device();
        device.setName(name);
        device.setAdditional(additional);
        device.setIdentifier(identifier);
        device.setEntities(entities);
        return device;
    }

}
