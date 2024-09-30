package com.milesight.iot.sample.complex.rule;

import com.milesight.iot.tmp.core.rule.api.Processor;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leon
 */
@Slf4j
public class DemoEntityExchangeProcessor implements Processor<ExchangePayload> {

    @Override
    public void processor(ExchangePayload exchange) {
        log.info(exchange.toString());

    }
}
