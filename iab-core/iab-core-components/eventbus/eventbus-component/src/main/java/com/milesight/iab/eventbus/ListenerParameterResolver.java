package com.milesight.iab.eventbus;

import com.milesight.iab.base.exception.ConfigurationException;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author leon
 */
public class ListenerParameterResolver {

    public <T extends Event<? extends IdentityKey>> T resolveEvent(T event){
        // If it is Event type, return ExchangeEvent
//        if(ExchangePayload.class.isAssignableFrom(event.getPayload().getClass())){
//            return (T) new ExchangeEvent(event.getEventType(), event.getPayload());
//        }
        return null;
    }

    public Class resolveEventType(Method method){
        if (method.getParameterTypes().length == 0) {
            throw new ConfigurationException("EventBus method param-number invalid, method:" + method);
        }

        Class<?> clazz = method.getParameterTypes()[0];

        Assert.isTrue(Event.class.isAssignableFrom(clazz), "The EventBus method input parameter must be an implementation of Event, or an implementation of Event containing generic parameters, method:" + method.toGenericString());

        Class<?> eventClass = clazz.isInterface() ? resolveParameterTypes(method) : clazz;

        Assert.notNull(eventClass, "The EventBus method input parameter must be an implementation of Event, or an implementation of Event containing generic parameters, method:" + method.toGenericString());

        return eventClass;
    }

    private Class<?> resolveParameterTypes(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        if (genericParameterTypes[0] instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) genericParameterTypes[0];

            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            if (actualTypeArguments.length == 1) {
                Class<?> actualTypeArgument = (Class<?>) actualTypeArguments[0];
                Assert.isTrue(IdentityKey.class.isAssignableFrom(actualTypeArgument), "parameter type must be an implementation of IdentityKey, method:" + method.toGenericString());
                // If it is ExchangePayload type, return ExchangeEvent
                if(ExchangePayload.class.isAssignableFrom(actualTypeArgument)){
                    return ExchangeEvent.class;
                }
                return actualTypeArgument;
            }
        }
        return null;
    }

}
