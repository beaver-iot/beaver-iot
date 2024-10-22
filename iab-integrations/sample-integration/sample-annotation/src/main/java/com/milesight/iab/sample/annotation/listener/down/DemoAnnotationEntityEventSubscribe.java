package com.milesight.iab.sample.annotation.listener.down;


import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.sample.annotation.entity.DemoDeviceEntities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoAnnotationEntityEventSubscribe {

    //支持表达式
    @EventSubscribe(payloadKeyExpression ="msc-integration.device.*.accessKey", eventType = ExchangeEvent.EventType.DOWN)
    public void subscribeProperty(Event<DemoDeviceEntities> event) {
        log.info("DemoEntityEventSubscribe subscribeProperty:{}",event);
    }

    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.syncHistory")
    public void subscribeProperties(ExchangeEvent event) {
        log.info("DemoEntityEventSubscribe subscribeProperties:{}",event);
    }
}
