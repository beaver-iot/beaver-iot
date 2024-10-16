package com.milesight.iab.context.configuration;

import com.milesight.iab.context.integration.entity.annotation.AnnotationEntityLoader;
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
}
