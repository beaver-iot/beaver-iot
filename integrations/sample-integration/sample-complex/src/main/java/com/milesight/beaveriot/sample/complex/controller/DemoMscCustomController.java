package com.milesight.beaveriot.sample.complex.controller;

import com.milesight.beaveriot.context.api.EntityServiceProvider;
import com.milesight.beaveriot.context.api.ExchangeFlowExecutor;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.eventbus.EventBus;
import com.milesight.beaveriot.sample.complex.entity.DemoMscServiceEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author leon
 */
@RestController
public class DemoMscCustomController {

    private EntityServiceProvider entityServiceProvider;
    @Autowired
    private ExchangeFlowExecutor exchangeFlowExecutor;
    @Autowired
    private EventBus eventBus;
    @PostMapping("/public/testConnect")
    public String testConnect(@RequestBody DemoMscServiceEntities.DemoMscSettingEntities demoMscSettingEntities) {
        // save entities to latest database
        entityServiceProvider.saveExchange(ExchangePayload.createFrom(demoMscSettingEntities));

        // call testConnect

        // generate token
        entityServiceProvider.saveExchange(ExchangePayload.create("msc-integration.integration.token", "xxxxxxx"));

        return "success";
    }

    @PostMapping("/public/receiveWebhook")
    public String receiveWebhook(@RequestBody ExchangePayload mscExchangePayload) {

        exchangeFlowExecutor.syncExchangeUp(mscExchangePayload);

        return "success";
    }


}
