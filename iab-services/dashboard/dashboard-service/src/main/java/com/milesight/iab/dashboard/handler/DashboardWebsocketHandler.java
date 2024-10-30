package com.milesight.iab.dashboard.handler;

import com.milesight.iab.authentication.facade.IAuthenticationFacade;
import com.milesight.iab.websocket.AbstractWebSocketHandler;
import com.milesight.iab.websocket.WebSocketContext;
import com.milesight.iab.websocket.WebSocketProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author loong
 * @date 2024/10/18 11:19
 */
@Component
@Getter
@Slf4j
public class DashboardWebsocketHandler extends AbstractWebSocketHandler {

    private final IAuthenticationFacade authenticationFacade;

    public DashboardWebsocketHandler(WebSocketProperties webSocketProperties, IAuthenticationFacade authenticationFacade) {
        super(webSocketProperties);
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, FullHttpRequest request, Map<String, List<String>> urlParams) throws Exception {
        String token = getToken(request);
        if (token == null) {
            sendHttpResponse(ctx, request, HttpResponseStatus.FORBIDDEN);
            return;
        }
        String userId = authenticationFacade.getUserIdByToken(token);
        if (userId == null) {
            sendHttpResponse(ctx, request, HttpResponseStatus.FORBIDDEN);
            return;
        }
        WebSocketContext.addChannel(userId, ctx);
    }

    @Override
    public void handleTextMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //FIXME user entity key to subscribe
        String userId = WebSocketContext.getChannelByValue(ctx);
        log.info("userId:{}, handleTextMessage:{}", userId, msg.text());
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx) throws Exception {
        WebSocketContext.removeChannelByValue(ctx);
    }

    @Override
    public void exception(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    private String getToken(FullHttpRequest request) {
        String authorizationValue = request.headers().get("Authorization");

        if (authorizationValue != null && authorizationValue.startsWith("Bearer ")) {
            return authorizationValue.substring(7);
        }
        return null;
    }

}
