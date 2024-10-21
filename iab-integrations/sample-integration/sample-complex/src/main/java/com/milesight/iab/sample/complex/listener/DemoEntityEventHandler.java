package com.milesight.iab.sample.complex.listener;


import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventHandler;

/**
 * @author leon
 */
public class DemoEntityEventHandler {

    //connect service
    @EventHandler(payloadKey="msc-integration.integration.addDevice", eventType = ExchangeEvent.EventType.DOWN)
    public String handleConnect(ExchangeEvent event) {

        return "success";
    }

    @EventHandler(payloadKey="msc-integration.integration.connect2", eventType = ExchangeEvent.EventType.DOWN)
    public void handleConnect2(ExchangeEvent event) {
    }

}
