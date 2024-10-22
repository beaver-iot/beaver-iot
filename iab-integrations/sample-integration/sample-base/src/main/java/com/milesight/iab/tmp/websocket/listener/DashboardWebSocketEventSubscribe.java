package com.milesight.iab.tmp.websocket.listener;


import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DashboardWebSocketEventSubscribe {
    //支持表达式
    @EventSubscribe(payloadKeyExpression ="*.device.*")
    public void subscribeProperty(Event<ExchangePayload> event) {
        log.info("DashboardWebSocketEventSubscribe subscribeProperty:{}", event.getPayload().getAllPayloads());
    }


}
