package com.milesight.iab.sample.complex.controller;

import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.ExchangeFlowExecutor;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.eventbus.api.EventResponse;
import com.milesight.iab.sample.complex.entity.DemoMscServiceEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author leon
 */
@RestController
public class DemoCommonController {

    @Autowired
    private ExchangeFlowExecutor exchangeFlowExecutor;
    @Autowired
    private EventBus eventBus;

    @PostMapping("/public/test-eventbus/{sync}")
    public EventResponse eventBusTest2(@RequestBody ExchangePayload payload, @PathVariable("sync") boolean sync) throws Exception {
        EventResponse response = null;
        if(sync){
            response = eventBus.handle(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, payload));
        }else{
            eventBus.publish(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, payload));
        }
        return response;
    }



    @PostMapping("/public/test-rule-flow/{sync}")
    public Object eventBusTest3(@RequestBody ExchangePayload payload, @PathVariable("sync") boolean sync) throws Exception {
        EventResponse response = null;
        if(sync){
            response = exchangeFlowExecutor.syncExchangeDown(payload);
        }else{
            exchangeFlowExecutor.asyncExchangeDown(payload);
        }
        return response;
    }



}
