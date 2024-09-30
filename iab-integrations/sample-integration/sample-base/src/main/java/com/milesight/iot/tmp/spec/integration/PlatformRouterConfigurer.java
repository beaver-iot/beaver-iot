package com.milesight.iot.tmp.spec.integration;

import org.apache.camel.CamelContext;

/**
 * @author leon
 */
public interface PlatformRouterConfigurer {

   void route(CamelContext context) throws Exception;

}

