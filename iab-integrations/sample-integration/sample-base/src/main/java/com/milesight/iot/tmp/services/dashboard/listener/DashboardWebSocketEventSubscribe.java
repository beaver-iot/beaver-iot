package com.milesight.iot.tmp.services.dashboard.listener;

import com.milesight.iot.tmp.core.eventbus.annotation.EventSubscribe;
import com.milesight.iot.tmp.spec.base.event.Event;
import com.milesight.iot.tmp.spec.entity.event.ExchangeEvent;

/**
 * @author leon
 */
public class DashboardWebSocketEventSubscribe {
    //支持表达式
    @EventSubscribe(payloadKey ="msc-integration.device.*.accessKey")
    public void subscribeProperty(Event<ExchangeEvent> event) {
    }

    @EventSubscribe(payloadKey ="msc-integration.integration.accessKey")
    public void subscribeProperties(Event<ExchangeEvent> event) {

    }
}
