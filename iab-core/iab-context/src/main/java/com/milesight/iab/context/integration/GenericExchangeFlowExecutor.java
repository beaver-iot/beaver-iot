package com.milesight.iab.context.integration;

import com.milesight.iab.context.api.ExchangeFlowExecutor;
import com.milesight.iab.context.integration.model.EventContextAccessor;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.api.EventResponse;
import com.milesight.iab.rule.RuleEngineExecutor;
import com.milesight.iab.rule.constants.RuleNodeNames;

/**
 * @author leon
 */
public class GenericExchangeFlowExecutor implements ExchangeFlowExecutor {

    private RuleEngineExecutor ruleEngineExecutor;

    public GenericExchangeFlowExecutor(RuleEngineExecutor ruleEngineExecutor) {
        this.ruleEngineExecutor = ruleEngineExecutor;
    }

    @Override
    public EventResponse syncExchangeUp(ExchangePayload exchangePayload) {
        ExchangeEvent event = ExchangeEvent.of(ExchangeEvent.EventType.UP, exchangePayload);
        initializeEventContext(event, true);
        return (EventResponse) ruleEngineExecutor.executeWithResponse(RuleNodeNames.innerExchangeUpFlow, event);
    }

    @Override
    public void asyncExchangeUp(ExchangePayload exchangePayload) {
        ExchangeEvent event = ExchangeEvent.of(ExchangeEvent.EventType.UP, exchangePayload);
        initializeEventContext(event, false);
        ruleEngineExecutor.execute(RuleNodeNames.innerExchangeUpFlow, event);
    }

    @Override
    public EventResponse syncExchangeDown(ExchangePayload exchangePayload) {
        ExchangeEvent event = ExchangeEvent.of(ExchangeEvent.EventType.DOWN, exchangePayload);
        initializeEventContext(event, true);
        return (EventResponse) ruleEngineExecutor.executeWithResponse(RuleNodeNames.innerExchangeDownFlow, event);
    }

    @Override
    public void asyncExchangeDown(ExchangePayload exchangePayload) {
        ExchangeEvent event = ExchangeEvent.of(ExchangeEvent.EventType.DOWN, exchangePayload);
        initializeEventContext(event, false);
        ruleEngineExecutor.execute(RuleNodeNames.innerExchangeDownFlow, event);
    }

    private void initializeEventContext(ExchangeEvent message, boolean syncCall) {
        ExchangePayload payload = message.getPayload();
        if(syncCall){
            payload.putContext(EventContextAccessor.EXCHANGE_KEY_SYNC_CALL, true);
        }else{
            payload.putContext(EventContextAccessor.EXCHANGE_KEY_SYNC_CALL, false);
        }
        payload.putContext(EventContextAccessor.EXCHANGE_KEY_EVENT_TYPE, message.getEventType());
    }
}
