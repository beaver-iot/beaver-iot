package com.milesight.iot.sample.annotation;

import com.milesight.iot.sample.annotation.rule.DemoEntityExchangeTransformer;
import com.milesight.iot.tmp.spec.integration.IntegrationBootstrap;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
public class AnnotationSampleIntegrationBootstrap extends IntegrationBootstrap {
    @Override
    public void route(CamelContext context) throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                //上行
                from("undertow:{{entity:msc-integration.integration.url}}")
                        .bean(DemoEntityExchangeTransformer.class)
                        .to("direct:genericDeviceExchangeUpFlow");

                //下行
                from("direct:mscDemoDeviceExchangeDownFlow")
                        .bean("jsonExchangeTransformer")
                        .to("http://localhost:8080/msc-xxx");
            }
        });
    }
}
