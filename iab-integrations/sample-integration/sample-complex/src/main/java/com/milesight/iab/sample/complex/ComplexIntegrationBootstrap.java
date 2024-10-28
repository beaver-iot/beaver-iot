package com.milesight.iab.sample.complex;


import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.builder.EntityBuilder;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.rule.constants.RuleNodeNames;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
public class ComplexIntegrationBootstrap implements IntegrationBootstrap {
    @Override
    public void onPrepared(Integration integrationConfig) {
        //ie: build device/entity by DeviceBuilder
        Entity entityConfig = new EntityBuilder()
                .property("prop_parent", AccessMod.W)
                .valueType(EntityValueType.STRING)
                .children()
                .valueType(EntityValueType.STRING).property("prop_children1", AccessMod.W).end()
                .children()
                .valueType(EntityValueType.STRING).property("prop_children2", AccessMod.W).end()
                .build();
        Device device = new DeviceBuilder()
                .name("complexDevice1")
                .identifier("complexDevice1")
                .entity(entityConfig)
                .build();
        integrationConfig.addInitialDevice(device);
    }

    @Override
    public void onStarted(Integration integrationConfig) {

    }

    @Override
    public void customizeRoute(CamelContext context) throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //演示上行采用规则引擎编程式定义流程：

                // 1. webhook:  定义一个简单的令牌验证方法
                String validToken = "your_bearer_token_here";
                // 配置路由
//                from("undertow:http://0.0.0.0:8089/webhook")
//                    // 提取 Authorization 头
//                    .setHeader("Authorization", simple("${header.Authorization}"))
//                    // 进行令牌验证
//                    .choice()
//                        .when(header("Authorization").isEqualTo("Bearer " + validToken))
//                        .bean("demoMscExchangeTransformer")
//                        .to(RuleNodeNames.innerExchangeUpFlow)
//                    .otherwise()
//                        .log("Invalid token, denying access")
//                        .setHeader("CamelHttpResponseCode", constant(401))
//                        .setBody(constant("Unauthorized"))
//                    .end();

                // 2. 定时pull
//                from("timer:foo?period=5000000")
//                    .bean("demoMscPullExchangeSource")
//                    .bean("demoMscExchangeTransformer")
//                    .to(RuleNodeNames.innerExchangeUpFlow);

            }
        });
    }

    @Override
    public void onDestroy(Integration integrationConfig) {

    }
}
