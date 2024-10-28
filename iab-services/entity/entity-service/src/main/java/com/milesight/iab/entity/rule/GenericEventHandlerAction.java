package com.milesight.iab.entity.rule;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.eventbus.api.EventResponse;
import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.TransformerNode;
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
@RuleNode(name = RuleNodeNames.innerEventHandlerAction, description = "innerEventHandlerAction")
public class GenericEventHandlerAction implements TransformerNode<ExchangePayload, EventResponse> {

    @Autowired
    private EventBus eventBus;

    @Override
    public EventResponse transform(ExchangePayload exchange) {

        log.debug("GenericEventHandlerAction processor {}", exchange.toString());

        String eventType = (String) exchange.getContext(EXCHANGE_KEY_EVENT_TYPE);

        return (EventResponse) eventBus.handle(ExchangeEvent.of(eventType, exchange));
    }

}
