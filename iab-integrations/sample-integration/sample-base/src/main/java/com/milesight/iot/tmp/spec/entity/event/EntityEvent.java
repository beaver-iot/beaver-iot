package com.milesight.iot.tmp.spec.entity.event;


import com.milesight.iot.tmp.spec.base.event.Event;

/**
 * @author leon
 */
public class EntityEvent<T> implements Event<T> {
    @Override
    public String getEventType() {
        return null;
    }

    @Override
    public String getPayloadKey() {
        return null;
    }

    @Override
    public T getPayload() {
        return null;
    }
}
