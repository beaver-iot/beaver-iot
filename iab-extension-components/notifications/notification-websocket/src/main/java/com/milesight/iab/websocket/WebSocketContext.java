package com.milesight.iab.websocket;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;
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

    public static List<ChannelHandlerContext> getChannelsByKeys(List<String> keys) {
        return channelMap.entrySet().stream().filter(entry -> keys.contains(entry.getKey())).map(Map.Entry::getValue).toList();
    }

    public static List<ChannelHandlerContext> getAllChannel() {
        return channelMap.values().stream().toList();
    }

    public static void removeChannel(String key) {
        channelMap.remove(key);
    }

    public static String getChannelByValue(ChannelHandlerContext ctx) {
        return channelMap.entrySet().stream().filter(entry -> entry.getValue().channel().id().asLongText().equals(ctx.channel().id().asLongText())).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public static void removeChannelByValue(ChannelHandlerContext ctx) {
        channelMap.forEach((key, channelHandlerContext) -> {
            if (channelHandlerContext.channel().id().asLongText().equals(ctx.channel().id().asLongText())) {
                channelMap.remove(key);
            }
        });
    }

    public static void addChannel(String key, ChannelHandlerContext ctx) {
        channelMap.put(key, ctx);
    }

    public static void sendMessage(String key, String message) {
        ChannelHandlerContext ctx = channelMap.get(key);
        if (ctx != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(message);
        }
    }

    public static void sendMessage(List<String> keys, String message) {
        getChannelsByKeys(keys).forEach(ctx -> {
            if (ctx.channel().isActive()) {
                ctx.writeAndFlush(message);
            }
        });
    }

    public static void sendAllMessage(String message) {
        channelMap.values().forEach(ctx -> {
            if (ctx.channel().isActive()) {
                ctx.writeAndFlush(message);
            }
        });
    }

}
