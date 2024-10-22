package com.milesight.iab.eventbus.subscribe;


import com.milesight.iab.eventbus.EventInvoker;
import com.milesight.iab.eventbus.ListenerCacheKey;
import com.milesight.iab.eventbus.ListenerParameterResolver;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author leon
 */
public class EventSubscribeInvoker<T extends Event<? extends IdentityKey>> implements EventInvoker<T> {
    private Object bean;
    private Method executeMethod;
    private ListenerCacheKey listenerCacheKey;
    private ListenerParameterResolver parameterResolver;

    public EventSubscribeInvoker(Object bean, Method executeMethod,ListenerCacheKey listenerCacheKey,ListenerParameterResolver parameterResolver) {
        this.bean = bean;
        this.executeMethod = executeMethod;
        this.listenerCacheKey = listenerCacheKey;
        this.parameterResolver = parameterResolver;
    }

    @Override
    public Object invoke(T event) throws InvocationTargetException, IllegalAccessException {

        T resolveEvent = parameterResolver.resolveEvent(event);

        return executeMethod.invoke(bean, resolveEvent);
    }

    @Override
    public String toString() {
        return "EventSubscribeInvoker{" +
                "executeMethod=" + executeMethod.toGenericString() +
                ", listenerCacheKey=" + listenerCacheKey +
                '}';
    };
}