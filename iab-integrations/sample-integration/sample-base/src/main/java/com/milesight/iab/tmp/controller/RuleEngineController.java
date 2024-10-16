package com.milesight.iab.tmp.controller;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.rule.RuleEngineExecutor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author leon
 */
@RestController
public class RuleEngineController {

    @Autowired
    private CamelContext camelContext;

    @GetMapping("/api/v1/remove/{endpoint}")
    public String removeEndpoint(@PathVariable("endpoint") String endpoint) throws Exception {
        camelContext.getEndpoint(endpoint).stop();
//        camelContext.getRouteController().stopRoute(endpoint);
        return "success";
    }

}
