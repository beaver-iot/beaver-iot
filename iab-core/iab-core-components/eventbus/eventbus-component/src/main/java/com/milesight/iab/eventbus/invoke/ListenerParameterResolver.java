package com.milesight.iab.eventbus.invoke;

import com.milesight.iab.base.exception.ConfigurationException;
import com.milesight.iab.context.integration.entity.annotation.AnnotationEntityCache;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class ListenerParameterResolver {

    public <T extends Event<? extends IdentityKey>> T resolveEvent(@Nullable Class<?> parameterTypes, T event, String[] matchMultiKeys){
        if(parameterTypes != null && ExchangePayload.class.isAssignableFrom(parameterTypes)){

            //filter key
            ExchangePayload payload = (ExchangePayload) event.getPayload();
            ExchangePayload newPayload = ExchangePayload.createFrom(payload, List.of(matchMultiKeys));

            if(parameterTypes != null && ExchangePayload.class.isAssignableFrom(parameterTypes)){
                newPayload = new ExchangePayloadProxy(newPayload, (Class<? extends ExchangePayload>) parameterTypes).proxy();
            }
            return (T) ExchangeEvent.of(event.getEventType(), newPayload);
        }
        return event;
    }

    @SneakyThrows
    private Object newInstance(Class<? extends ExchangePayload> parameterTypes, ExchangePayload exchangePayload) {
        ExchangePayload newInstance = parameterTypes.newInstance();
        newInstance.setContext(exchangePayload.getContext());
        newInstance.setTimestamp(exchangePayload.getTimestamp());
        return newInstance;
    }

    public Class resolveActualEventType(Method method){
        if (method.getParameterTypes().length == 0) {
            throw new ConfigurationException("EventBus method param-number invalid, method:" + method);
        }

        Class<?> clazz = method.getParameterTypes()[0];

        Assert.isTrue(Event.class.isAssignableFrom(clazz), "The EventBus method input parameter must be an implementation of Event, or an implementation of Event containing generic parameters, method:" + method.toGenericString());

        Class<?> eventClass = clazz;
        if(clazz.isInterface()){
            Class<?> actualTypeArgument = resolveParameterTypes(method);
            if(ExchangePayload.class.isAssignableFrom(actualTypeArgument)){
                eventClass = ExchangeEvent.class;
            }
        }

        Assert.notNull(eventClass, "The EventBus method input parameter must be an implementation of Event, or an implementation of Event containing generic parameters, method:" + method.toGenericString());

        return eventClass;
    }

    public Class<?> resolveParameterTypes(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        if (genericParameterTypes[0] instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) genericParameterTypes[0];

            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            if (actualTypeArguments.length == 1) {
                Class<?> actualTypeArgument = (Class<?>) actualTypeArguments[0];
                Assert.isTrue(IdentityKey.class.isAssignableFrom(actualTypeArgument), "parameter type must be an implementation of IdentityKey, method:" + method.toGenericString());
                return actualTypeArgument;
            }
        }
        return null;
    }

    public class ExchangePayloadProxy {
        private final Class<? extends ExchangePayload> parameterTypes;
        private final ExchangePayload exchangePayload;

        public ExchangePayloadProxy(ExchangePayload exchangePayload, Class<? extends ExchangePayload> parameterTypes) {
            this.exchangePayload = exchangePayload;
            this.parameterTypes = parameterTypes;
        }

        public ExchangePayload proxy() {
            Map<String, Object> allPayloads = exchangePayload.getAllPayloads();
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(newInstance(parameterTypes, exchangePayload));
            factory.addAdvice((MethodInterceptor) invocation -> {
                Method method = invocation.getMethod();
                if ("toString".equals(method.getName())) {
                    return exchangePayload.toString();
                }else if("hashCode".equals(method.getName()) || "equals".equals(method.getName())){
                    return invocation.proceed();
                }
                String cacheEntityKey = AnnotationEntityCache.INSTANCE.getEntityKeyByMethod(method);
                if(allPayloads.containsKey(cacheEntityKey)){
                    return allPayloads.get(cacheEntityKey);
                }
                return invocation.proceed();
            });
            return (ExchangePayload) factory.getProxy();
        }
    }


}
