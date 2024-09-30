package com.milesight.iot.sample.complex.rule;

import com.milesight.iot.tmp.core.rule.api.Transformer;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

/**
 * @author leon
 */
@Slf4j
public class DemoEntityExchangeTransformer implements Transformer<Exchange, ExchangePayload> {
    @Override
    public ExchangePayload transform(Exchange exchange) {
        log.info(exchange.toString());
        return new ExchangePayload();
    }
}
