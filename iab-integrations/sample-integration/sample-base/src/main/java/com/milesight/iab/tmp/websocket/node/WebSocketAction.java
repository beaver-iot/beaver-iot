package com.milesight.iab.tmp.websocket.node;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.ProcessorNode;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leon
 */
@Slf4j
@RuleNode(name = "WebSocketAction", description = "WebSocket action")
public class WebSocketAction implements ProcessorNode<ExchangePayloadAccessor> {
    @Override
    public void processor(ExchangePayloadAccessor exchange) {
        log.info(exchange.toString());
    }
}
