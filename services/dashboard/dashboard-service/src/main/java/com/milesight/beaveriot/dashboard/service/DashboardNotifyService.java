package com.milesight.beaveriot.dashboard.service;

import com.milesight.beaveriot.base.utils.JsonUtils;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.context.integration.model.event.ExchangeEvent;
import com.milesight.beaveriot.context.integration.model.event.WebSocketEvent;
import com.milesight.beaveriot.eventbus.annotations.EventSubscribe;
import com.milesight.beaveriot.websocket.WebSocketContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/18 11:15
 */
@Service
@Slf4j
public class DashboardNotifyService {

    @EventSubscribe(payloadKeyExpression = ".*")
    public void onDeviceDashboardNotify(ExchangeEvent exchangeEvent) {
        doDashboardNotify(exchangeEvent.getPayload());
    }

    private void doDashboardNotify(ExchangePayload exchangePayload) {
        try {
            log.info("onDashboardNotify:{}", exchangePayload);
            //FIXME send message to user that has entity show in dashboard
            WebSocketEvent webSocketEvent = WebSocketEvent.of(WebSocketEvent.EventType.EXCHANGE, exchangePayload);
            WebSocketContext.sendAllMessage(JsonUtils.toJSON(webSocketEvent));
        } catch (Exception e) {
            log.error("onDashboardNotify error:{}", e.getMessage(), e);
        }
    }

}
