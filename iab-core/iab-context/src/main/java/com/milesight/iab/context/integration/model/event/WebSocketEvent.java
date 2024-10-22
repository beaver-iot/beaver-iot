package com.milesight.iab.context.integration.model.event;


import lombok.Getter;

/**
 * @author leon
 */
@Getter
public record WebSocketEvent(String eventType, Object payload) {

    public static WebSocketEvent of(String eventType, Object payload) {
        return new WebSocketEvent(eventType, payload);
    }

    public static class EventType {
        private EventType() {
        }

        public static final String EXCHANGE = "Exchange";
    }

}
