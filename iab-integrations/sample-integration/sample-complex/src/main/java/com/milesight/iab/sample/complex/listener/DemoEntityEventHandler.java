package com.milesight.iab.sample.complex.listener;


import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoEntityEventHandler {

    //connect service
    @EventSubscribe(payloadKeyExpression="msc-integration.integration.connect", async = false)
    public EventResponse handleConnect(ExchangeEvent event) {

        log.debug("DemoEntityEventHandler handleConnect:{}",event);
        return EventResponse.of("msc-integration.integration.connect", "connect success");
    }

    @EventSubscribe(payloadKeyExpression="msc-integration.device.{sn}.connect2.key", eventType = ExchangeEvent.EventType.DOWN)
    public void handleConnect2(ExchangeEvent event) {
        log.debug("DemoEntityEventHandler handleConnect2:{}",event);
    }

}
