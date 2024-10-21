package com.milesight.iab.context.integration.model;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import lombok.Getter;
import lombok.Setter;
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
        if(!CollectionUtils.isEmpty(initialDevices)){
            initialDevices.forEach(device -> device.initializeProperties(this));
        }

        if(!CollectionUtils.isEmpty(initialEntities)){
            initialEntities.forEach(entity -> entity.initializeProperties(this));
        }
    }


    public boolean validate() {
        //todo
        //1. Duplicate integrations
        //2. Legality of attributes、config
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

}
