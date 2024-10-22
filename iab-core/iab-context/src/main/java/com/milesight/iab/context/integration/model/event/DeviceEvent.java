package com.milesight.iab.context.integration.model.event;


import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.eventbus.api.IdentityKey;

/**
 * @author leon
 */
public class DeviceEvent implements Event<Device> {

    private Device device;
    private String eventType;

    public DeviceEvent(){
    }
    public DeviceEvent(String eventType, Device device) {
        this.eventType = eventType;
        this.device = device;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public void setPayload(IdentityKey payload) {
        this.device = (Device) payload;
    }

    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public Device getPayload() {
        return device;
    }

    public static DeviceEvent of(String eventType, Device device) {
        return new DeviceEvent(eventType, device);
    }
    public static class EventType{

        private EventType() {
        }
        public static final String CREATED = "Created";
        public static final String UPDATED = "Updated";
        public static final String DELETED = "Deleted";
    }

}
