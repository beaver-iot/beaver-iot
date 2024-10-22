package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceBuilder is a builder class for Device, eg:
 *         Entity entityConfig = new EntityBuilder()
 *                 .property("humidity", AccessMod.RW)
 *                 .identifier("humidity")
 *                     .children()
 *                         .property("value", AccessMod.RW)
 *                         .end()
 *                     .children()
 *                         .property("unit", AccessMod.RW)
 *                         .end()
 *                     .children()
 *                         .property("timestamp", AccessMod.RW)
 *                         .end()
 *                 .build();
 *
 *         Device device = new DeviceBuilder("myIntegrationId"，"myIntegration")
 *                             .name("myDevice")
 *                             .identifier("mySN")
 *                             .entity(entityConfig)
 *                             .build();
 * @author leon
 */
public class DeviceBuilder {

    private List<Entity> entities;
    private String name;
    private String identifier;
    private Map<String,Object> additional;
    private IntegrationBuilder integrationBuilder;
    private Integration integration;
    private Long id;

    public DeviceBuilder(IntegrationBuilder integrationBuilder) {
        this.integrationBuilder = integrationBuilder;
        this.integration = integrationBuilder.integration;
    }

    public DeviceBuilder(Integration integration){
        this.integration = integration;
    }

    public DeviceBuilder(String integrationId, String integrationName){
        this.integration = Integration.of(integrationId, integrationName);
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
        integrationBuilder.integration.addInitialDevice(build());
        return integrationBuilder;
    }

    public DeviceBuilder id(Long id) {
        this.id = id;
        return this;
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
        device.setIntegration(integration);
        device.initializeProperties(integration);
        device.setId(id);
        return device;
    }

}
