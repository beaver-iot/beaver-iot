package com.milesight.iot.sample.complex.listener;


import com.milesight.iot.sample.complex.entity.DemoDeviceEntities;
import com.milesight.iot.tmp.core.eventbus.annotation.EventSubscribe;
import com.milesight.iot.tmp.spec.base.event.Event;
import com.milesight.iot.tmp.spec.entity.enums.ExchangeEventType;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;

/**
 * @author leon
 */
public class DemoEntityEventSubscribe {

    //支持表达式
    @EventSubscribe(payloadKey ="msc-integration.device.*.accessKey", eventType = ExchangeEventType.DOWN)
    public void subscribeProperty(Event<DemoDeviceEntities> event) {
    }

    @EventSubscribe(payloadKey ="msc-integration.integration.accessKey")
    public void subscribeProperties(Event<ExchangePayload> event) {
    }
}
