package com.milesight.iab.context.integration.model.event;


import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.ExchangePayload;

/**
 * @author leon
 */
public class ExchangeEvent implements Event<ExchangePayload> {

    private ExchangePayload exchangePayload;
    private String eventType;

    public ExchangeEvent(String eventType, ExchangePayload exchangePayload) {
        this.eventType = eventType;
        this.exchangePayload = exchangePayload;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public ExchangePayload getPayload() {
        return exchangePayload;
    }

    public static ExchangeEvent of(String eventType, ExchangePayload exchangePayload) {
        return new ExchangeEvent(eventType, exchangePayload);
    }

    public static class EventType{
        private EventType() {
        }
        public static final String DOWN = "Down";
        public static final String UP = "Up";
    }
}
