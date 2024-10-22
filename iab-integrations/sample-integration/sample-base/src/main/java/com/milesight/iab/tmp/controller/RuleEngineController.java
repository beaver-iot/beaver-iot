package com.milesight.iab.tmp.controller;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.EventBus;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leon
 */
@RestController
public class RuleEngineController {

    @Autowired
    private CamelContext camelContext;
    @Autowired
    private EventBus eventBus;

    @GetMapping("/api/v1/remove/{endpoint}")
    public String removeEndpoint(@PathVariable("endpoint") String endpoint) throws Exception {
        camelContext.getEndpoint(endpoint).stop();
//        camelContext.getRouteController().stopRoute(endpoint);
        return "success";
    }

    @GetMapping("/api/v1/eventbus/{key}")
    public String eventBusTest(@PathVariable("key") String key) throws Exception {
        eventBus.publish(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, ExchangePayload.create(key, "test")));
//        camelContext.getRouteController().stopRoute(endpoint);
        return "success";
    }

}
