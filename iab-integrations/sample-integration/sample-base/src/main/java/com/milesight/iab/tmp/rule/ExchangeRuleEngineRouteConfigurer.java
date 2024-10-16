package com.milesight.iab.tmp.rule;

import com.milesight.iab.rule.RuleEngineRouteConfigurer;
import com.milesight.iab.rule.constants.RuleNodeNames;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
public class ExchangeRuleEngineRouteConfigurer implements RuleEngineRouteConfigurer {
    @Override
    public void customizeRoute(CamelContext context) throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                //Generic RuleEngine sub flow (before exchange down may be need to do some business logic, eg: transform data, etc.):
                // 0. validate exchange and cache EntityMetadata?, filter invalid exchange
                // 1. exchange split, group entities
                // 2. save exchange current value to latest database
                // 3. save exchange current value to history database
                // 4. exchange payload type
                //    4.1 service : call EventHandler RuleNode
                //    4.2 event or properties : call EventSubscribe RuleNode
                // 5. business logic: EventSubscribe or EventHandler
                from(RuleNodeNames.innerExchangeUpFlow )
                    .choice()
                        .when().method(RuleNodeNames.innerExchangeValidator)
                            .split().method(RuleNodeNames.innerExchangeSplitter)
                            .bean(RuleNodeNames.innerLatestExchangeSaveAction)
                            .bean(RuleNodeNames.innerHistoryExchangeSaveAction)
                            .choice()
                                //service sync call
                                .when().method(RuleNodeNames.innerSyncCallPredicate)
                                     //包含自定义的规则节点
                                    .bean(RuleNodeNames.innerEventHandlerAction)
                                .otherwise()
                                    .bean(RuleNodeNames.innerEventSubscribeAction)
                                .end()
                             .endChoice()
                        .otherwise()
                            .log("ExchangeValidator failed........")
                        .end();

                //
                from(RuleNodeNames.innerExchangeDownFlow)
                    .choice()
                        .when().method(RuleNodeNames.innerExchangeValidator)
                        .split().method(RuleNodeNames.innerExchangeSplitter)
                        .bean(RuleNodeNames.innerLatestExchangeSaveAction)
                        .bean(RuleNodeNames.innerHistoryExchangeSaveAction)
                        .choice()
                            .when().method(RuleNodeNames.innerCustomExchangeDownHandlerPredicate)
                                .to("direct:innerExchangeDownHandler!msc-integration")
                            .when().method(RuleNodeNames.innerSyncCallPredicate)
//                        .when().groovy("request.header.get('callMod').equals('sync')")  //service sync call
                                .bean(RuleNodeNames.innerEventHandlerAction)
                            .otherwise()
                                .bean(RuleNodeNames.innerEventSubscribeAction)
                            .end()
                        .endChoice()
                    .otherwise()
                        .log("ExchangeValidator failed........")
                    .end();
            }
        });
    }
}
