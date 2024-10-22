package com.milesight.iab.dashboard.service;

import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
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
    public void onDashboardNotify(ExchangeEvent event) {
        //TODO
//        WebSocketEvent webSocketEvent = WebSocketEvent.of(WebSocketEvent.EventType.EXCHANGE, event.getPayload());
//        WebSocketContext.sendMessage(, webSocketEvent);
    }


}
