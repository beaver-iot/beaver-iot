package com.milesight.iab.sample.complex.controller;

import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.rule.RuleEngineExecutor;
import com.milesight.iab.sample.complex.entity.DemoMscSettingEntities;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leon
 */
@RestController
public class DemoMscCustomController {

    private EntityServiceProvider entityServiceProvider;
    private RuleEngineExecutor ruleEngineExecutor;

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

    private ExchangePayload toExchangePayload(MscExchangePayload mscExchangePayload) {
        return null;
    }
    public class MscExchangePayload {
    }
}
