package com.milesight.iab.tmp.rule.event;


import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.Device;

/**
 * @author leon
 */
public class WebSocketEvent implements Event<ExchangePayload> {
    @Override
    public String getEventType() {
        return null;
    }

    @Override
    public String getPayloadKey() {
        return null;
    }

    @Override
    public ExchangePayload getPayload() {
        return null;
    }
}
