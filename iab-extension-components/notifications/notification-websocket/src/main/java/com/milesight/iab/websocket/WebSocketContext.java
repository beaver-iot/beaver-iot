package com.milesight.iab.websocket;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author loong
 * @date 2024/10/18 14:42
 */
public class WebSocketContext {

    private static final Map<String, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();

    public static ChannelHandlerContext getChannel(String key) {
        return channelMap.get(key);
    }

    public static void removeChannel(String key) {
        channelMap.remove(key);
    }

    public static void removeChannelByValue(ChannelHandlerContext ctx){
        channelMap.forEach((token, channelHandlerContext) -> {
            if (channelHandlerContext.equals(ctx)) {
                channelMap.remove(token);
            }
        });
    }

    public static void addChannel(String key, ChannelHandlerContext ctx) {
        channelMap.put(key, ctx);
    }

    public static void sendMessage(String key, String message) {
        ChannelHandlerContext ctx = channelMap.get(key);
        if (ctx != null) {
            ctx.writeAndFlush(message);
        }
    }

}
