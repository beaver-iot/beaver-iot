package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.api.EventResponse;

/**
 * @author leon
 */
public interface ExchangeFlowExecutor {

    EventResponse syncExchangeUp(ExchangePayload exchangeEvent);

    void asyncExchangeUp(ExchangePayload exchangeEvent);

    EventResponse syncExchangeDown(ExchangePayload exchangeEvent);

    void asyncExchangeDown(ExchangePayload exchangeEvent);

}
