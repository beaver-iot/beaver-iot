package com.milesight.iab.context.integration.model.event;


/**
 * @author leon
 */
public class WebSocketEvent {

    private Object payload;
    private String eventType;

    public WebSocketEvent(String eventType, Object payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public static WebSocketEvent of(String eventType, Object payload) {
        return new WebSocketEvent(eventType, payload);
    }

    public static class EventType{
        private EventType() {
        }
        public static final String EXCHANGE = "Exchange";
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayloadKey() {
        return null;
    }

    public Object getPayload() {
        return payload;
    }

}
