package com.milesight.iab.sample.annotation.listener.down;


import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.sample.annotation.entity.DemoDeviceEntities;

/**
 * @author leon
 */
public class DemoEntityEventSubscribe {

    //支持表达式
    @EventSubscribe(payloadKeyExpression ="msc-integration.device.*.accessKey", eventType = ExchangeEvent.EventType.DOWN)
    public void subscribeProperty(Event<DemoDeviceEntities> event) {
    }

    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.accessKey")
    public void subscribeProperties(ExchangeEvent event) {
    }
}
