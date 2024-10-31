package com.milesight.beaveriot.sample.complex.rule;

import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.rule.annotations.RuleNode;
import com.milesight.beaveriot.rule.api.ProcessorNode;
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
