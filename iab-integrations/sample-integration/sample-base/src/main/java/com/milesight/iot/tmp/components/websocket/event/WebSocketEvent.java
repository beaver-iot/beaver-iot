package com.milesight.iot.tmp.components.websocket.event;


import com.milesight.iot.tmp.spec.base.event.Event;
import com.milesight.iot.tmp.spec.entity.model.Device;

/**
 * @author leon
 */
public class WebSocketEvent implements Event<Device> {
    @Override
    public String getEventType() {
        return null;
    }

    @Override
    public String getPayloadKey() {
        return null;
    }

    @Override
    public Device getPayload() {
        return null;
    }
}
