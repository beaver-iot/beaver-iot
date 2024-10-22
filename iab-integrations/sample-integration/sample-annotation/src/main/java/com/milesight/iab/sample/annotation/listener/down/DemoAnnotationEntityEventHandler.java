package com.milesight.iab.sample.annotation.listener.down;


import com.milesight.iab.eventbus.annotations.EventHandler;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.ExchangePayload;
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
    @EventHandler(payloadKey="*.integration.connect")
    public DemoDeviceEntities handleService(Event<DemoDeviceEntities> event) {
        log.debug("DemoAnnotationEntityEventHandler handleService:{}",event);
        return null;
    }

    @EventHandler(payloadKey="msc-integration.device.demoSN.changeStatus")
    public DemoDeviceEntities handleDeviceService(Event<DemoIntegrationEntities> event) {
        log.debug("DemoAnnotationEntityEventHandler handleDeviceService:{}",event);
        return null;
    }

    @EventHandler(payloadKey="${integration.name}.device.demoSN.demo.groupProperties")
    public DemoDeviceEntities handleDeviceServices(Event<ExchangePayload> event) {
        log.debug("DemoAnnotationEntityEventHandler handleDeviceServices:{}",event);
        return null;
    }

}
