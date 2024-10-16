package com.milesight.iab.sample.annotation.listener.down;


import com.milesight.iab.eventbus.annotations.EventHandler;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;
import com.milesight.iab.sample.annotation.entity.DemoDeviceEntities;
import com.milesight.iab.sample.annotation.entity.DemoIntegrationEntities;

/**
 * @author leon
 */
public class DemoEntityEventHandler {

    //仅支持精确匹配
    @EventHandler(payloadKey="*.integration.connect")
    public DemoDeviceEntities handleService(Event<DemoDeviceEntities> event) {
        return null;
    }

    @EventHandler(payloadKey="msc-integration.device.demoSN.changeStatus")
    public DemoDeviceEntities handleDeviceService(DemoIntegrationEntities event) {
        return null;
    }

    @EventHandler(payloadKey="${integration.name}.device.demoSN.demo.groupProperties")
    public DemoDeviceEntities handleDeviceServices(Event<ExchangePayload> event) {
        return null;
    }

}
