package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
public class EntityBuilder extends BaseEntityBuilder<EntityBuilder>{
    protected List<Entity> children = new ArrayList<>();

    public EntityBuilder() {
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
