package com.milesight.iab.tmp.controller;

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

    @GetMapping("/api/v1/remove/{endpoint}")
    public String removeEndpoint(@PathVariable("endpoint") String endpoint) throws Exception {
        camelContext.getEndpoint(endpoint).stop();
//        camelContext.getRouteController().stopRoute(endpoint);
        return "success";
    }

}
