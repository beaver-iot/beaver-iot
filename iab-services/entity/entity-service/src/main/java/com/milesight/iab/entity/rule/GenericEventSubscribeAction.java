package com.milesight.iab.entity.rule;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.ProcessorNode;
import com.milesight.iab.rule.constants.RuleNodeNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.milesight.iab.context.constants.ExchangeContextKeys.EXCHANGE_KEY_EVENT_TYPE;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = RuleNodeNames.innerEventSubscribeAction, description = "innerEventSubscribeAction")
public class GenericEventSubscribeAction implements ProcessorNode<ExchangePayload> {

    @Autowired
    private EventBus eventBus;

    @Override
    public void processor(ExchangePayload exchange) {

        log.debug("GenericEventSubscribeAction processor {}", exchange.toString());

        String eventType = (String) exchange.getContext(EXCHANGE_KEY_EVENT_TYPE);

        eventBus.publish(ExchangeEvent.of(eventType, exchange));
    }
}
