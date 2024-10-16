package com.milesight.iab.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author loong
 * @date 2024/10/8 10:40
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final String websocketPath;
    private final AbstractWebSocketHandler webSocketHandler;

    public WebSocketChannelInitializer(String websocketPath, AbstractWebSocketHandler webSocketHandler) {
        this.websocketPath = websocketPath;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(webSocketHandler);
        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath));
    }

}
