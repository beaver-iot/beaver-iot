package com.milesight.iab.eventbus.configuration;

import com.lmax.disruptor.dsl.Disruptor;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.eventbus.AnnotationEventBusRegister;
import com.milesight.iab.eventbus.ListenerParameterResolver;
import com.milesight.iab.eventbus.handler.EventHandlerDispatcher;
import com.milesight.iab.eventbus.subscribe.DisruptorEventBus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author leon
 */
@ConditionalOnClass(Disruptor.class)
@Configuration
@EnableConfigurationProperties(DisruptorOptions.class)
public class EventBusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AnnotationEventBusRegister disruptorEventBusRegister(EventHandlerDispatcher eventHandlerDispatcher, DisruptorEventBus eventBus){
        return new AnnotationEventBusRegister(eventHandlerDispatcher, eventBus);
    }

    @Bean
    @ConditionalOnMissingBean
    public DisruptorEventBus eventBus(DisruptorOptions disruptorOptions, EventHandlerDispatcher eventHandlerDispatcher,ListenerParameterResolver listenerParameterResolver){
        return new DisruptorEventBus(disruptorOptions, eventHandlerDispatcher,listenerParameterResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventHandlerDispatcher eventHandlerDispatcher(ListenerParameterResolver listenerParameterResolver){
        return new EventHandlerDispatcher(listenerParameterResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ListenerParameterResolver listenerParameterResolver(){
        return new ListenerParameterResolver();
    }

    @Bean
    public Executor eventBusTaskExecutor(DisruptorOptions disruptorOptions) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(disruptorOptions.getCorePoolSize());
        executor.setMaxPoolSize(disruptorOptions.getMaxPoolSize());
        executor.setQueueCapacity(disruptorOptions.getQueueCapacity());
        executor.setThreadNamePrefix("eventBus-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}