package com.milesight.iab.sample.annotation.listener.down;


import com.milesight.iab.context.integration.entity.annotation.AnnotationEntityCache;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.api.IdentityKey;
import com.milesight.iab.sample.annotation.entity.DemoDeviceEntities;
import com.milesight.iab.sample.annotation.entity.DemoIntegrationEntities;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;import org.springframework.aop.framework.ProxyFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoAnnotationEntityEventSubscribe {


    @EventSubscribe(payloadKeyExpression ="anno-integration.integration.connect.accessKey", eventType = ExchangeEvent.EventType.DOWN)
    public void subscribeIntegrationProperty(ExchangeEvent event) {
        ExchangePayload payload = event.getPayload();
        log.warn(">>>>subscribeIntegrationProperty:" + payload);
        log.info("DemoEntityEventSubscribe subscribeProperty:{}",event);
    }

    @EventSubscribe(payloadKeyExpression ="anno-integration.integration.connect.*", eventType = ExchangeEvent.EventType.DOWN)
    public void subscribeIntegrationProperty(Event<DemoIntegrationEntities.DemoGroupSettingEntities> event) {
        DemoIntegrationEntities.DemoGroupSettingEntities payload = event.getPayload();
        String changeStatus = payload.getAccessKey();
        String secretKey = payload.getSecretKey();
        log.warn(">>>>>>>>>>>>>>" + changeStatus + secretKey);
        log.info("DemoEntityEventSubscribe subscribeProperty:{}",event);
    }

    //支持表达式
    @EventSubscribe(payloadKeyExpression ="anno-integration.device.demoSN.changeStatus", eventType = ExchangeEvent.EventType.DOWN)
    public void subscribeProperty(Event<DemoDeviceEntities> event) {
        DemoDeviceEntities payload = event.getPayload();
        String changeStatus = payload.getChangeStatus();
        System.out.println(">>>>>>>>>>>>>>" + changeStatus);
        log.info("DemoEntityEventSubscribe subscribeProperty:{}",event);
    }

    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.syncHistory")
    public void subscribeProperties(ExchangeEvent event) {
        log.info("DemoEntityEventSubscribe subscribeProperties:{}",event);
    }

    //同步调用,可不返回值
//    @EventSubscribe(payloadKeyExpression ="msc-integration.*.syncHistory", async=false)
//    public void syncSubscribeProperties(ExchangeEvent event) {
//        log.info("DemoEntityEventSubscribe subscribeProperties:{}",event);
//    }
//
//    //可以返回，返回指定的对象EventResponse
//    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.syncHistory", async=false)
//    public EventResponse syncSubscribeProperties(ExchangeEvent event) {
//        log.info("DemoEntityEventSubscribe subscribeProperties:{}",event);
//        return EventResponse.of("key", "value");
//    }
//
//    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.syncHistory", payloadContext={@Expression(name="entityType",value="property")})
//    public EventResponse syncSubscribeProperties(ExchangeEvent event) {
//        log.info("DemoEntityEventSubscribe subscribeProperties:{}",event);
//        return EventResponse.of("key", "value");
//    }



}
