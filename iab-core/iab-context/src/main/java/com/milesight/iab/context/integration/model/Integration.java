package com.milesight.iab.context.integration.model;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * iab-extension-components
 * @author leon
 */
@Getter
@Setter
public class Integration {

    private String name;

    private String description;

    private boolean enabled = true;

    private Class<? extends IntegrationBootstrap> integrationClass;

    private String flowExchangeDownHandler;

    private String entityIdentifierAddDevice;

    private String entityIdentifierDeleteDevice;

    private List<Device> devices = new ArrayList<>();

    private List<Entity> entities = new ArrayList<>();

    public Integration() {
    }
    public Integration(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Integration of(String name, String description){
        return new Integration(name, description);
    }

    public void initializeProperties() {
        //relation entities and devices
        if(!CollectionUtils.isEmpty(devices)){
            devices.forEach(device -> {
                device.setIntegration(this);
                List<Entity> entitiesList = device.getEntities();
                initializeEntityProperties(entitiesList, device);
                if(!CollectionUtils.isEmpty(entitiesList)){
                    for (Entity entity : entitiesList) {
                        initializeEntityProperties(entity.getChildren(), device);
                    }
                }
            });
        }

        if(!CollectionUtils.isEmpty(entities)){
            initializeEntityProperties(entities, null);
            for (Entity entity : entities) {
                initializeEntityProperties(entity.getChildren(), null);
            }
        }
    }

    private void initializeEntityProperties(List<Entity> entitiesList, Device device){
        if(!CollectionUtils.isEmpty(entitiesList)){
            entitiesList.forEach(entity -> {
                entity.setDevice(device);
                entity.setIntegration(this);
            });
        }
    }

    public boolean validate() {
        //todo
        //1. Duplicate integrations
        //2. Legality of attributes、config
        return true;
    }

    public void addEntity(Entity entity) {
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.add(entity);
    }
    public void addEntities(List<Entity> addEntities) {
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.addAll(addEntities);
    }

    public void addDevice(Device device) {
        if(devices == null){
            devices = new ArrayList<>();
        }
        devices.add(device);
    }

}
