package com.milesight.iab.eventbus.handler;

import com.milesight.iab.eventbus.EventInvoker;
import com.milesight.iab.eventbus.ListenerParameterResolver;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author leon
 */
public class EventHandleInvoker<T extends Event<? extends IdentityKey>> implements EventInvoker<T> {

    private Object bean;
    private Method executeMethod;

    private ListenerParameterResolver parameterResolver;

    public EventHandleInvoker(Object bean, Method executeMethod, ListenerParameterResolver listenerParameterResolver) {
        this.bean = bean;
        this.executeMethod = executeMethod;
        this.parameterResolver = listenerParameterResolver;
    }

    @Override
    public Object invoke(T event) throws InvocationTargetException, IllegalAccessException {

        T resolveEvent = parameterResolver.resolveEvent(event);

        return executeMethod.invoke(bean, resolveEvent);
    }
}