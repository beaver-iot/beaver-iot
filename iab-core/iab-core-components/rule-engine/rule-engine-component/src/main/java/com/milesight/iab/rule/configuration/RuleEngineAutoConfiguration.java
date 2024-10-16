package com.milesight.iab.rule.configuration;

import com.milesight.iab.rule.RuleEngineRouteConfigurer;
import com.milesight.iab.rule.flow.CamelRuleEngineExecutor;
import com.milesight.iab.rule.flow.RuleEngineRunner;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leon
 */
@Configuration
public class RuleEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CamelRuleEngineExecutor ruleEngineExecutor() {
        return new CamelRuleEngineExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleEngineRunner ruleEngineRunner(ObjectProvider<RuleEngineRouteConfigurer> ruleEngineRouteConfigurers, CamelRuleEngineExecutor ruleEngineExecutor, CamelContext context){
        return new RuleEngineRunner(ruleEngineRouteConfigurers,ruleEngineExecutor, context);
    }
}
