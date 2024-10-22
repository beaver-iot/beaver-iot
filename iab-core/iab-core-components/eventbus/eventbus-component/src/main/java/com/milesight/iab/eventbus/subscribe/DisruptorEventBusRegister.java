package com.milesight.iab.eventbus.subscribe;

import com.milesight.iab.base.exception.ConfigurationException;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.configuration.DisruptorOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * @author leon
 */
@Slf4j
public class DisruptorEventBusRegister implements ApplicationContextAware, /*SmartInitializingSingleton,*/ DisposableBean {
    private static ApplicationContext applicationContext;

    private DisruptorOptions disruptorOptions;

    public DisruptorEventBusRegister(DisruptorOptions disruptorOptions) {
        this.disruptorOptions = disruptorOptions;
    }

//    @Override
    public void afterSingletonsInstantiated() {
        registerDisruptorEventBus(applicationContext);
    }

    // destroy
    @Override
    public void destroy() {
        EventBusFactory.disruptor().shutdown();
    }

    private void registerDisruptorEventBus(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = null;
            Lazy onBean = applicationContext.findAnnotationOnBean(beanDefinitionName, Lazy.class);
            if (onBean!=null){
                log.debug("EventSubscribe annotation scan, skip @Lazy Bean:{}", beanDefinitionName);
                continue;
            }else {
                bean = applicationContext.getBean(beanDefinitionName);
            }

            // filter method
            Map<Method, EventSubscribe> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<EventSubscribe>) method -> AnnotatedElementUtils.findMergedAnnotation(method, EventSubscribe.class));
            } catch (Throwable ex) {
                log.error("EventSubscribe method-jobhandler resolve error for bean[" + beanDefinitionName + "].", ex);
            }
            if (annotatedMethods==null || annotatedMethods.isEmpty()) {
                continue;
            }

            // generate and register method job handler
            for (Map.Entry<Method, EventSubscribe> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                EventSubscribe eventSubscribe = methodXxlJobEntry.getValue();
                registEventBus(eventSubscribe, bean, executeMethod,disruptorOptions);
            }

        }
    }

    private void registEventBus(EventSubscribe eventSubscribe, Object bean, Method executeMethod,DisruptorOptions disruptorOptions) {
        // valid
        if (executeMethod.getParameterTypes().length == 0) {
            throw new ConfigurationException("EventSubscribe method param-number invalid, method:" + executeMethod);
        }
//        if (eventSubscribe.value().length >= 1 && !executeMethod.getParameterTypes()[0].isAssignableFrom(eventSubscribe.value()[0])) {
//            throw new RuntimeException("EventSubscribe method param-type invalid, method:" + executeMethod);
//        }

        Class clazz = /*eventSubscribe.value().length >= 1 ? eventSubscribe.value()[1] :*/ executeMethod.getParameterTypes()[0];
        // subscribe
        ThreadPoolTaskExecutor executor =  obtainExecutor(eventSubscribe, disruptorOptions);

        DisruptorEventBus.getInstance(disruptorOptions).subscribe(clazz, (event) -> {
            try {
                executeMethod.invoke(bean, event);
            } catch (Exception e) {
                log.error("EventSubscribe method invoke error, method:" + executeMethod, e);
            }
        },executor);
    }

    private ThreadPoolTaskExecutor obtainExecutor(EventSubscribe eventSubscribe,DisruptorOptions disruptorOptions) {
        if(StringUtils.hasText(eventSubscribe.executor())){
            return (ThreadPoolTaskExecutor) applicationContext.getBean(eventSubscribe.executor());
        }
        //todo
//        int maxSize = eventSubscribe.maxPoolSize() != 0 ? eventSubscribe.maxPoolSize() : disruptorOptions.getMaxPoolSize();
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(0); // 核心线程数
//        executor.setMaxPoolSize(maxSize); // 最大线程数
//        executor.setQueueCapacity(0); // 队列容量
//        executor.setKeepAliveSeconds(60); // 线程空闲时间
//        executor.setThreadNamePrefix("eventbus-"); // 线程名称前缀
//        executor.setRejectedExecutionHandler(newInstance(eventSubscribe.exceptionHandler()));
//        executor.initialize();
        return null;
    }

    private RejectedExecutionHandler newInstance(Class<? extends RejectedExecutionHandler> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
             throw new RuntimeException("EventSubscribe RejectedExecutionHandler newInstance error",e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)  {
        DisruptorEventBusRegister.applicationContext = applicationContext;
    }

}