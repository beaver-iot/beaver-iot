package com.milesight.iab.tmp.rule.node;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.ProcessorNode;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;
import com.milesight.iab.rule.constants.RuleNodeNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = RuleNodeNames.innerEventHandlerAction, description = "innerEventHandlerAction")
public class EventHandlerAction implements ProcessorNode<ExchangePayload> {
    @Override
    public void processor(ExchangePayload exchange) {
        log.info("EventHandlerAction processor {}",exchange.toString());

    }
}
