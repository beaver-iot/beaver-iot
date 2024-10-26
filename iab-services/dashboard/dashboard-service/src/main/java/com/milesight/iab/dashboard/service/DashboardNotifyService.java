package com.milesight.iab.dashboard.service;

import com.milesight.iab.base.utils.JsonUtils;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.context.integration.model.event.WebSocketEvent;
import com.milesight.iab.context.security.SecurityUserContext;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.websocket.WebSocketContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/18 11:15
 */
@Service
@Slf4j
public class DashboardNotifyService {

    @EventSubscribe(payloadKeyExpression = "*.device.*")
    public void onDeviceDashboardNotify(ExchangeEvent exchangeEvent) {
        doDashboardNotify(exchangeEvent.getPayload());
    }

    @EventSubscribe(payloadKeyExpression = "*.integration.*")
    public void onIntegrationDashboardNotify(ExchangeEvent exchangeEvent) {
        doDashboardNotify(exchangeEvent.getPayload());
    }

    private void doDashboardNotify(ExchangePayload exchangePayload) {
        try {
            log.info("onDashboardNotify:{}", exchangePayload);
            if (exchangePayload.getContext(SecurityUserContext.USER_ID) == null) {
                log.error("onDashboardNotify userId is null");
                return;
            }
            String userId = exchangePayload.getContext(SecurityUserContext.USER_ID).toString();
            WebSocketEvent webSocketEvent = WebSocketEvent.of(WebSocketEvent.EventType.EXCHANGE, exchangePayload);
            WebSocketContext.sendMessage(userId, JsonUtils.toJSON(webSocketEvent));
        } catch (Exception e) {
            log.error("onDashboardNotify error:{}", e.getMessage(), e);
        }
    }

}
