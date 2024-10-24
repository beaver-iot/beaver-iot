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
        initializeEventContext(ExchangeEvent.EventType.UP, exchangePayload, true);
        return (EventResponse) ruleEngineExecutor.executeWithResponse(RuleNodeNames.innerExchangeUpFlow, exchangePayload);
    }

    @Override
    public void asyncExchangeUp(ExchangePayload exchangePayload) {
        initializeEventContext(ExchangeEvent.EventType.UP, exchangePayload,false);
        ruleEngineExecutor.execute(RuleNodeNames.innerExchangeUpFlow, exchangePayload);
    }

    @Override
    public EventResponse syncExchangeDown(ExchangePayload exchangePayload) {
        initializeEventContext(ExchangeEvent.EventType.DOWN, exchangePayload, true);
        return (EventResponse) ruleEngineExecutor.executeWithResponse(RuleNodeNames.innerExchangeDownFlow, exchangePayload);
    }

    @Override
    public void asyncExchangeDown(ExchangePayload exchangePayload) {
        initializeEventContext(ExchangeEvent.EventType.DOWN, exchangePayload, false);
        ruleEngineExecutor.execute(RuleNodeNames.innerExchangeDownFlow, exchangePayload);
    }

    private void initializeEventContext(String eventType, ExchangePayload payload, boolean syncCall) {
        if(syncCall){
            payload.putContext(EventContextAccessor.EXCHANGE_KEY_SYNC_CALL, true);
        }else{
            payload.putContext(EventContextAccessor.EXCHANGE_KEY_SYNC_CALL, false);
        }
        payload.putContext(EventContextAccessor.EXCHANGE_KEY_EVENT_TYPE, eventType);
    }
}
