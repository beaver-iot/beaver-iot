package com.milesight.iab.context.integration.model;

import com.milesight.iab.context.constants.IntegrationConstants;
import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
@Getter
@Setter
public class Integration {

    private String id;

    private String name;

    private String description;

    private String iconUrl;

    private boolean enabled = true;

    private Class<? extends IntegrationBootstrap> integrationClass;

    private String flowExchangeDownHandler;

    private String entityIdentifierAddDevice;

    private String entityIdentifierDeleteDevice;

    private List<Device> initialDevices = new ArrayList<>();

    private List<Entity> initialEntities = new ArrayList<>();

    public Integration() {
    }
    public Integration(String id, String name, String description, String iconUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public static Integration of(String id,String name){
        return new Integration(id, name, null,null);
    }

    public static Integration of(String id,String name, String description){
        return new Integration(id, name, description,null);
    }

    public static Integration of(String id,String name, String description, String iconUrl){
        return new Integration(id, name, description, iconUrl);
    }

    public void initializeProperties() {

        validate();

        if(!CollectionUtils.isEmpty(initialDevices)){
            initialDevices.forEach(device -> device.initializeProperties(this.getId()));
        }

        if(!CollectionUtils.isEmpty(initialEntities)){
            initialEntities.forEach(entity -> entity.initializeProperties(this.getId()));
        }
    }


    public boolean validate() {

        Assert.notNull(name, "Integration name must not be null");
        
        return true;
    }

    public void addInitialEntity(Entity entity) {
        if(initialEntities == null){
            initialEntities = new ArrayList<>();
        }
        initialEntities.add(entity);
    }
    public void addInitialEntities(List<Entity> addEntities) {
        if(initialEntities == null){
            initialEntities = new ArrayList<>();
        }
        initialEntities.addAll(addEntities);
    }

    public void addInitialDevice(Device device) {
        if(initialDevices == null){
            initialDevices = new ArrayList<>();
        }
        initialDevices.add(device);
    }

    public String getEntityKeyAddDevice() {
        String addDeviceServiceIdentifier = this.getEntityIdentifierAddDevice();
        if (addDeviceServiceIdentifier == null) {
            return null;
        }

        return IntegrationConstants.formatIntegrationEntityKey(this.getId(), this.getEntityIdentifierAddDevice());
    }

}
