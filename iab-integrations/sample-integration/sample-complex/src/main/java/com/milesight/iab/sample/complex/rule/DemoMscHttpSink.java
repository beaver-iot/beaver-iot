package com.milesight.iab.sample.complex.rule;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.ProcessorNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = "demoMscHttpSink")
public class DemoMscHttpSink implements ProcessorNode<ExchangePayload> {

    @Override
    public void processor(ExchangePayload exchange) {
        log.info("demoMscHttpSink call {}" ,exchange.toString());
    }
}
