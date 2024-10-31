package com.milesight.beaveriot.eventbus.invoke;


import com.milesight.beaveriot.eventbus.api.Event;
import com.milesight.beaveriot.eventbus.api.IdentityKey;
import jakarta.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author leon
 */
public class EventSubscribeInvoker<T extends Event<? extends IdentityKey>> implements EventInvoker<T> {
    private final Class<?> parameterTypes;
    private final Object bean;
    private final Method executeMethod;
    private final ListenerParameterResolver parameterResolver;

    public EventSubscribeInvoker(Object bean, Method executeMethod,  @Nullable Class<?> parameterTypes, ListenerParameterResolver parameterResolver) {
        this.bean = bean;
        this.executeMethod = executeMethod;
        this.parameterTypes = parameterTypes;
        this.parameterResolver = parameterResolver;
    }

    @Override
    public Object invoke(T event, String[] matchMultiKeys) throws InvocationTargetException, IllegalAccessException {

        T resolveEvent = parameterResolver.resolveEvent(parameterTypes, event, matchMultiKeys);

        return executeMethod.invoke(bean, resolveEvent);
    }

    @Override
    public String toString() {
        return "EventSubscribeInvoker{" +
                "executeMethod=" + executeMethod.toGenericString() +
                '}';
    };
}