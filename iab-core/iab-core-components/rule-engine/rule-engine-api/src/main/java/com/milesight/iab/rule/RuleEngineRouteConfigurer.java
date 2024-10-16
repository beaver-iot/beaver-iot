package com.milesight.iab.rule;

import org.apache.camel.CamelContext;

/**
 * @author leon
 */
public interface RuleEngineRouteConfigurer {

   void customizeRoute(CamelContext context) throws Exception;

}

