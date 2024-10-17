package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
public class EntityBuilder extends BaseEntityBuilder<EntityBuilder>{

    private Integration integration;

    private Device device;

    protected List<Entity> children = new ArrayList<>();

    public EntityBuilder(){
    }
    public EntityBuilder(Integration integration) {
        this.integration = integration;
    }

    public EntityBuilder(String integrationName) {
        this.integration = Integration.of(integrationName, null);
    }

    public EntityBuilder(Device device) {
        this.device = device;
    }

    public EntityBuilder children(Entity childrenEntity) {
        children.add(childrenEntity);
        return this;
    }

    public EntityBuilder children(List<Entity> childrenEntities) {
        children.addAll(childrenEntities);
        return this;
    }

    public ChildEntityBuilder children() {
        return new ChildEntityBuilder(this);
    }

    public Entity build() {
        Entity entity = newInstance();
        entity.setChildren(children);
        if(integration != null){
            entity.initializeProperties(integration);
        }else if(device != null){
            entity.initializeProperties(device.getIntegration(), device);
        }
        return entity;
    }

    public class ChildEntityBuilder extends BaseEntityBuilder<ChildEntityBuilder>{

        private EntityBuilder entityBuilder;

        public ChildEntityBuilder(EntityBuilder entityBuilder) {
            this.entityBuilder = entityBuilder;
            this.group = entityBuilder.identifier;
        }

        public EntityBuilder end() {
            Entity entity = newInstance();
            entityBuilder.children(entity);
            return entityBuilder;
        }
    }
}
