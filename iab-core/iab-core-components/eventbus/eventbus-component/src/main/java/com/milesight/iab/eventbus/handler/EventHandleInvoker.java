package com.milesight.iab.eventbus.handler;

import com.milesight.iab.eventbus.EventInvoker;
import com.milesight.iab.eventbus.ListenerParameterResolver;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;
import jakarta.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author leon
 */
public class EventHandleInvoker<T extends Event<? extends IdentityKey>> implements EventInvoker<T> {

    /**
     * The parameter types of the method
     */
    private final Class<?> parameterTypes;
    private final Object bean;
    private final Method executeMethod;
    private final ListenerParameterResolver parameterResolver;

    public EventHandleInvoker(Object bean, Method executeMethod, @Nullable Class<?> parameterTypes, ListenerParameterResolver listenerParameterResolver) {
        this.bean = bean;
        this.executeMethod = executeMethod;
        this.parameterTypes = parameterTypes;
        this.parameterResolver = listenerParameterResolver;
    }

    @Override
    public Object invoke(T event, String[] matchMultiKeys) throws InvocationTargetException, IllegalAccessException {

        T resolveEvent = parameterResolver.resolveEvent(parameterTypes, event, matchMultiKeys);

        return executeMethod.invoke(bean, resolveEvent);
    }
}