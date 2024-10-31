package com.milesight.beaveriot.sample.annotation.listener.down;


import com.milesight.beaveriot.context.integration.model.event.ExchangeEvent;
import com.milesight.beaveriot.eventbus.annotations.EventSubscribe;
import com.milesight.beaveriot.eventbus.api.Event;
import com.milesight.beaveriot.eventbus.api.EventResponse;
import com.milesight.beaveriot.sample.annotation.entity.DemoIntegrationEntities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoAnnotationIntegrationEntitySubscribe {

    //仅支持精确匹配
    @EventSubscribe(payloadKeyExpression="demo-anno-integration.integration.connect.*", eventType= ExchangeEvent.EventType.DOWN)
    public EventResponse onConnect(Event<DemoIntegrationEntities.DemoGroupSettingEntities> event) {
        log.debug("DemoAnnotationIntegrationEntitySubscribe onConnect, AccessKey :{}, SecretKey {}",event.getPayload().getAccessKey(),event.getPayload().getSecretKey());
        return EventResponse.of("connectResult",event.getPayload().getAllPayloads());
    }

    @EventSubscribe(payloadKeyExpression="demo-anno-integration.integration.connect.deviceSync")
    public void onDeviceSync(ExchangeEvent event) {
        log.debug("DemoAnnotationIntegrationEntitySubscribe onDeviceSync:{}",event);
    }

    @EventSubscribe(payloadKeyExpression="demo-anno-integration.integration.connect.entitySync")
    public void onEntitySync(Event<DemoIntegrationEntities> event) {
        log.debug("DemoAnnotationIntegrationEntitySubscribe onEntitySync:{}",event);
    }


}
