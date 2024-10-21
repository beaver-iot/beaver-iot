package com.milesight.iab.eventbus.configuration;

import com.lmax.disruptor.dsl.Disruptor;
import com.milesight.iab.eventbus.DisruptorEventBusRegister;
import com.milesight.iab.eventbus.DisruptorOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leon
 */
@ConditionalOnClass(Disruptor.class)
@Configuration
@EnableConfigurationProperties(DisruptorOptions.class)
public class EventBusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DisruptorEventBusRegister disruptorEventBusRegister(DisruptorOptions disruptorOptions){
        return new DisruptorEventBusRegister(disruptorOptions);
    }
}
