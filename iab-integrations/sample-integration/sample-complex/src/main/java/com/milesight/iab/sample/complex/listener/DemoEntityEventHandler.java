package com.milesight.iab.sample.complex.listener;


import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoEntityEventHandler {

    //connect service
    @EventHandler(payloadKey="msc-integration.integration.connect", eventType = ExchangeEvent.EventType.DOWN)
    public String handleConnect(ExchangeEvent event) {

        log.debug("DemoEntityEventHandler handleConnect:{}",event);
        return "success";
    }

    @EventHandler(payloadKey="msc-integration.device.{sn}.connect2.key", eventType = ExchangeEvent.EventType.DOWN)
    public void handleConnect2(ExchangeEvent event) {
        log.debug("DemoEntityEventHandler handleConnect2:{}",event);
    }

}
