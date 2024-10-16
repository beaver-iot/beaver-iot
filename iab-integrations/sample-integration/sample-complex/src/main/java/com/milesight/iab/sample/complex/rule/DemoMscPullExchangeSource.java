package com.milesight.iab.sample.complex.rule;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.ProcessorNode;
import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = "demoMscPullExchangeSource")
public class DemoMscPullExchangeSource implements ProcessorNode<Exchange> {

    @Override
    public void processor(Exchange exchange) {
        ExchangePayload exchangePayload = ExchangePayload.create("timer.b.c1", "123");
        exchangePayload.put("timer.b.c2", "456");
        exchange.getIn().setBody(exchangePayload);
        log.info("==============timer call demoMscPullExchangeSource, size:{}==============",exchangePayload.size());
    }
}
