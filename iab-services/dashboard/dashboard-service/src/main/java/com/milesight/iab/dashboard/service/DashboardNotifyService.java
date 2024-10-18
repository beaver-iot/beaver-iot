package com.milesight.iab.dashboard.service;

import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.dashboard.handler.DashboardWebsocketHandler;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.websocket.WebSocketChannelInitializer;
import com.milesight.iab.websocket.WebSocketNettyServer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/18 11:15
 */
@Service
@Slf4j
public class DashboardNotifyService {

    @PostConstruct
    public void init() {
        //TODO
        int dashboardWebsocketPort = 8088;
        String dashboardWebsocketPath = "/dashboard/notify";

        DashboardWebsocketHandler dashboardWebsocketHandler = new DashboardWebsocketHandler(dashboardWebsocketPath);
        WebSocketChannelInitializer dashboardWebSocketChannelInitializer = new WebSocketChannelInitializer(dashboardWebsocketPath, dashboardWebsocketHandler);
        WebSocketNettyServer websocketNettyServer = new WebSocketNettyServer();
        try {
            websocketNettyServer.start(dashboardWebsocketPort, dashboardWebSocketChannelInitializer);
        } catch (Exception e) {
            log.error("init websocket error:{}", e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @EventSubscribe
    public void onDashboardNotify(ExchangeEvent event) {
        //TODO

//        WebsocketContext.sendMessage(,);
    }


}
