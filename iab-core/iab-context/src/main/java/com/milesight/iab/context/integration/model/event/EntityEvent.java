package com.milesight.iab.context.integration.model.event;


import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.Entity;

/**
 * @author leon
 */
public class EntityEvent implements Event<Entity> {

    private Entity entity;
    private String eventType;

    public EntityEvent(){
    }

    public EntityEvent(String eventType, Entity entity) {
        this.eventType = eventType;
        this.entity = entity;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public void setPayload(Entity payload) {
        this.entity = payload;
    }

    @Override
    public Entity getPayload() {
        return entity;
    }

    public static EntityEvent of(String eventType, Entity entity) {
        return new EntityEvent(eventType, entity);
    }

    public static class EventType{
        private EventType() {
        }
        public static final String CREATED = "Created";
        public static final String UPDATED = "Updated";
        public static final String DELETED = "Deleted";
    }
}
