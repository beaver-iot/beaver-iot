package com.milesight.iab.dashboard.service;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/18 11:15
 */
@Service
@Slf4j
public class DashboardNotifyService {

    @EventSubscribe
    public void onDashboardNotify(Event<ExchangePayload> event) {
        //TODO
//        WebSocketEvent webSocketEvent = WebSocketEvent.of(WebSocketEvent.EventType.EXCHANGE, event.getPayload());
//        WebSocketContext.sendMessage(, webSocketEvent);
    }


}
