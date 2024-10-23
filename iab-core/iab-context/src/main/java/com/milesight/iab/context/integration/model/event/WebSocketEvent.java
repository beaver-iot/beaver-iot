package com.milesight.iab.context.integration.model.event;


import lombok.Getter;

import java.io.Serializable;

/**
 * @author leon
 */
@Getter
public class WebSocketEvent implements Serializable {

    private final Object payload;
    private final String eventType;

    public WebSocketEvent(String eventType, Object payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public static WebSocketEvent of(String eventType, Object payload) {
        return new WebSocketEvent(eventType, payload);
    }

    public static class EventType {
        private EventType() {
        }

        public static final String EXCHANGE = "Exchange";
    }

}
