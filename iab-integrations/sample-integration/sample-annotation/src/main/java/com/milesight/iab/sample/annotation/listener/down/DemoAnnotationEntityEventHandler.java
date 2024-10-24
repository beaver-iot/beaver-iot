package com.milesight.iab.sample.annotation.listener.down;


import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.eventbus.api.EventResponse;
import com.milesight.iab.sample.annotation.entity.DemoDeviceEntities;
import com.milesight.iab.sample.annotation.entity.DemoIntegrationEntities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoAnnotationEntityEventHandler {

    //仅支持精确匹配
    @EventSubscribe(payloadKeyExpression="anno-integration.integration.connect.*", eventType= ExchangeEvent.EventType.DOWN, async = false)
    public EventResponse handleService(Event<DemoDeviceEntities> event) {
        log.debug("DemoAnnotationEntityEventHandler handleService:{}",event);
        return EventResponse.of("a1",new DemoDeviceEntities());
    }

    @EventSubscribe(payloadKeyExpression="anno-integration.integration.connect.accessKey", eventType= ExchangeEvent.EventType.DOWN, async = false)
    public EventResponse handleService2(ExchangeEvent event) {
        log.debug("DemoAnnotationEntityEventHandler handleService:{}",event);
        return EventResponse.of("a2","b");
    }


    @EventSubscribe(payloadKeyExpression="msc-integration.device.demoSN.changeStatus", eventType= ExchangeEvent.EventType.DOWN)
    public void handleDeviceService(Event<DemoIntegrationEntities> event) {
        DemoIntegrationEntities payload = event.getPayload();
        DemoIntegrationEntities.DemoGroupSettingEntities connect = payload.getConnect();
        log.debug("DemoAnnotationEntityEventHandler handleDeviceService:{}",event.getPayload());
    }

    @EventSubscribe(payloadKeyExpression="${integration.name}.device.demoSN.demo.groupProperties", eventType= ExchangeEvent.EventType.UP)
    public void handleDeviceServices(Event<ExchangePayload> event) {
        log.debug("DemoAnnotationEntityEventHandler handleDeviceServices:{}",event.getPayload());
    }

}
