package com.milesight.iab.context.configuration;

import com.milesight.iab.context.integration.GenericExchangeFlowExecutor;
import com.milesight.iab.context.integration.entity.annotation.AnnotationEntityLoader;
import com.milesight.iab.rule.RuleEngineExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leon
 */
@Configuration
public class EntityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AnnotationEntityLoader annotationEntityLoader(){
        return new AnnotationEntityLoader();
    }

    @Bean
    @ConditionalOnMissingBean
    public GenericExchangeFlowExecutor genericExchangeFlowExecutor(RuleEngineExecutor ruleEngineExecutor){
        return new GenericExchangeFlowExecutor(ruleEngineExecutor);
    }
}
