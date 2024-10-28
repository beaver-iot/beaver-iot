package com.milesight.iab.tmp.controller;

import com.milesight.iab.context.api.ExchangeFlowExecutor;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.support.YamlPropertySourceFactory;
import com.milesight.iab.eventbus.api.EventResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leon
 */
@RestController
public class DemoEntityExchangeController  {

    @Autowired
    private ExchangeFlowExecutor exchangeFlowExecutor;


    @SneakyThrows
    @GetMapping("/api/v1/entity/{id}/dynamic-form")
    public String dynamicForm() throws ClassNotFoundException {
        Class<?> aClass = DemoEntityExchangeController.class.getClassLoader().loadClass("com.milesight.iab.sample.annotation.AnnotationSampleIntegrationBootstrap");

        PropertySource<?> a = new YamlPropertySourceFactory().createJarPropertySource("a", aClass.getProtectionDomain().getCodeSource().getLocation().getPath());
        return "a";
    }

    @PostMapping("/api/v1/entity/exchange-down")
    public Object exchangeDown(@RequestBody ExchangePayload message){
        return  exchangeFlowExecutor.syncExchangeDown(message);

    }
    @PostMapping("/api/v1/entity/exchange-up")
    public EventResponse exchangeUp(@RequestBody ExchangePayload message){
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
    public EventResponse callExchangeUp(ExchangePayload exchangePayloads) {

        //Generic RuleEngine sub flow (before exchange down may be need to do some business logic, eg: transform data, etc.):
        // 1. validate exchange and cache EntityMetadata?, filter invalid exchange

        // 2. save exchange current value to database
        // 3. save exchange current value to history database
        // 4. exchange payload type
        //    4.1 service : call EventHandler RuleNode
        //    4.2 event or properties : call EventSubscribe RuleNode

        // 5. business logic: EventSubscribe or EventHandler

        return exchangeFlowExecutor.syncExchangeUp( exchangePayloads);
    }

}
