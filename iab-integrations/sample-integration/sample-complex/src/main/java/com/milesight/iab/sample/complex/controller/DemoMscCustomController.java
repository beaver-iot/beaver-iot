package com.milesight.iab.sample.complex.controller;

import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.rule.RuleEngineExecutor;
import com.milesight.iab.sample.complex.entity.DemoMscServiceEntities;
import com.milesight.iab.sample.complex.entity.DemoMscSettingEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author leon
 */
@RestController
public class DemoMscCustomController {

    private EntityServiceProvider entityServiceProvider;
    private RuleEngineExecutor ruleEngineExecutor;
    @Autowired
    private EventBus eventBus;
    @PostMapping("/testConnect")
    public String testConnect(@RequestBody DemoMscSettingEntities demoMscSettingEntities) {
        // save entities to latest database
        entityServiceProvider.saveExchange(ExchangePayload.createFrom(demoMscSettingEntities));

        // call testConnect
            //todo : call msc url

        // generate token
        entityServiceProvider.saveExchange(ExchangePayload.create("msc-integration.integration.token", "xxxxxxx"));

        return "success";
    }

    @PostMapping("/receiveWebhook")
    public String receiveWebhook(@RequestBody MscExchangePayload mscExchangePayload) {
        //1. validate token

        //2. data transform
        ExchangePayload exchangePayload = toExchangePayload(mscExchangePayload);
        //3. call rule engine
        ruleEngineExecutor.exchangeUp(exchangePayload);

        return "success";
    }

    @PostMapping("/api/v1/myevent/{type}")
    public String eventBusTest2(@RequestBody ExchangePayload payload) throws Exception {
        eventBus.publish(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, payload));
        return "success";
    }

    @PostMapping("/api/v1/handleEvent/{type}")
    public Object eventBusTest3(@RequestBody ExchangePayload payload) throws Exception {
        return eventBus.handle(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, payload));
    }

    @GetMapping("/api/v1/eventhandler/{key}")
    public Object eventHandleTest(@PathVariable("key") String key) throws Exception {
        Object test = eventBus.handle(ExchangeEvent.of(ExchangeEvent.EventType.DOWN, ExchangePayload.create(key, "test")));
//        camelContext.getRouteController().stopRoute(endpoint);
        return test;
    }

    private ExchangePayload toExchangePayload(MscExchangePayload mscExchangePayload) {
        return null;
    }
    public class MscExchangePayload {
    }
}
