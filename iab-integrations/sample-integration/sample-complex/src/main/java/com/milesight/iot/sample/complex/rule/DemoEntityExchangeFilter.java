package com.milesight.iot.sample.complex.rule;

import com.milesight.iot.tmp.core.rule.api.Predicate;
import com.milesight.iot.tmp.spec.entity.annotation.RuleNode;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

/**
 * @author leon
 */
@RuleNode(name = "demoEntityExchangeTransformer", description = "demo entity exchange transformer")
@Slf4j
public class DemoEntityExchangeFilter implements Predicate<ExchangePayload> {

    @Override
    public boolean matches(ExchangePayload exchange) {
        Exchange ex = null;
        log.info(exchange.toString());
        return true;
    }
}
