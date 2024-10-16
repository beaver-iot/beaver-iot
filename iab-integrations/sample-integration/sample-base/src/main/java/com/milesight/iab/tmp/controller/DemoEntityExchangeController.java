package com.milesight.iab.tmp.controller;

import com.milesight.iab.rule.RuleEngineExecutor;
import com.milesight.iab.context.integration.model.ExchangePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author leon
 */
@RestController
public class DemoEntityExchangeController  {

    @Autowired
    private RuleEngineExecutor ruleEngineExecutor;


    @GetMapping("/api/v1/entity/{id}/dynamic-form")
    public String dynamicForm(){
        return null;
    }
//    @PostMapping("/api/v1/entity/services/call")
//    public String callServices(List<ExchangePayload> message){
//        return callExchangeUp(message);
//    }
//    @PostMapping("/api/v1/entity/events/call")
//    public String callEvents(List<ExchangePayload> message){
//        return callExchangeUp(message);
//    }
//    @PostMapping("/api/v1/entity/properties/call")
//    public String callProperties(List<ExchangePayload> message){
//        return callExchangeUp(message);
//    }

    @PostMapping("/api/v1/entity/exchange-down")
    public Object exchangeDown(@RequestBody ExchangePayload message){
        return  ruleEngineExecutor.exchangeDown(message);

    }
    @PostMapping("/api/v1/entity/exchange-up")
    public ExchangePayload exchangeUp(@RequestBody ExchangePayload message){
        return callExchangeUp(message);
    }

    /**
     *   Generic RuleEngine sub flow (before exchange down may be need to do some business logic, eg: transform data, etc.):
     *   1. validate exchange and cache EntityMetadata?, filter invalid exchange
     *   2. save exchange current value to database
     *   3. save exchange current value to history database
     *   4. exchange payload type
     *      4.1 service : call EventHandler RuleNode
     *      4.2 event or properties : call EventSubscribe RuleNode
     *   5. business logic: EventSubscribe or EventHandler
     * @param exchangePayloads
     * @return
     */
    public ExchangePayload callExchangeUp(ExchangePayload exchangePayloads) {

        //Generic RuleEngine sub flow (before exchange down may be need to do some business logic, eg: transform data, etc.):
        // 1. validate exchange and cache EntityMetadata?, filter invalid exchange

        // 2. save exchange current value to database
        // 3. save exchange current value to history database
        // 4. exchange payload type
        //    4.1 service : call EventHandler RuleNode
        //    4.2 event or properties : call EventSubscribe RuleNode

        // 5. business logic: EventSubscribe or EventHandler

        return (ExchangePayload) ruleEngineExecutor.exchangeUp( exchangePayloads);
    }

}
