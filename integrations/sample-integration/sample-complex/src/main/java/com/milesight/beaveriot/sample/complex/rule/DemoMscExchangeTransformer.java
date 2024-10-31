package com.milesight.beaveriot.sample.complex.rule;

import com.milesight.beaveriot.rule.annotations.RuleNode;
import com.milesight.beaveriot.rule.api.TransformerNode;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = "demoMscExchangeTransformer", description = "")
public class DemoMscExchangeTransformer implements TransformerNode<Exchange, ExchangePayload> {
    @Override
    public ExchangePayload transform(Exchange exchange) {
        ExchangePayload body = exchange.getIn().getBody(ExchangePayload.class);
        log.info("demoMscExchangeTransformer transform {},",body);
        return body;
    }
}
