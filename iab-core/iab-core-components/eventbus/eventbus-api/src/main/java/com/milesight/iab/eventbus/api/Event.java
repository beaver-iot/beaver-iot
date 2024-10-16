package com.milesight.iab.eventbus.api;

/**
 * @author leon
 */
public interface Event<T extends IdentityKey> {

    String getEventType();

    default String getPayloadKey() {
        return getPayload().getKey();
    }

    T getPayload();
}
