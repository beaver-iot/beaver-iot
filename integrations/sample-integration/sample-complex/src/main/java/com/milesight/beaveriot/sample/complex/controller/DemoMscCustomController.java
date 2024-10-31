package com.milesight.beaveriot.sample.complex.controller;

import com.milesight.beaveriot.context.api.ExchangeFlowExecutor;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.eventbus.EventBus;
import com.milesight.beaveriot.sample.complex.entity.DemoMscServiceEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leon
 */
@RestController
public class DemoMscCustomController {

    @Autowired
    private ExchangeFlowExecutor exchangeFlowExecutor;
    @Autowired
    private EventBus eventBus;
    @PostMapping("/public/testConnect")
    public String testConnect(@RequestBody DemoMscServiceEntities.DemoMscSettingEntities demoMscSettingEntities) {
        // save entities to latest database

        // call testConnect

        // generate token

        return "success";
    }

    @PostMapping("/public/receiveWebhook")
    public String receiveWebhook(@RequestBody ExchangePayload mscExchangePayload) {

        exchangeFlowExecutor.syncExchangeUp(mscExchangePayload);

        return "success";
    }


}
