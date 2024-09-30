package com.milesight.iot.tmp.spec.base.event;

/**
 * @author leon
 */
public interface Event<T> {

    String getEventType();

    String getPayloadKey();

    T getPayload();
}
