package com.milesight.iab.eventbus.handler;

import com.milesight.iab.base.exception.ConfigurationException;
import com.milesight.iab.eventbus.EventInvoker;
import com.milesight.iab.eventbus.ListenerCacheKey;
import com.milesight.iab.eventbus.ListenerParameterResolver;
import com.milesight.iab.eventbus.annotations.EventHandler;
import com.milesight.iab.eventbus.api.Event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 */
public class EventHandlerDispatcher {

    private final ListenerParameterResolver parameterResolver;

    private Map<ListenerCacheKey, EventInvoker> handleListenerCache = new ConcurrentHashMap<>();

    public EventHandlerDispatcher(ListenerParameterResolver parameterResolver) {
        this.parameterResolver = new ListenerParameterResolver();
    }

    public <T extends Event> Object handle(T event){

        EventInvoker invoker = handleListenerCache.get(new ListenerCacheKey(event.getPayloadKey(), event.getEventType()));
        if(invoker == null){
            throw new ConfigurationException("No handler found for event, please check your configuration: " + event.getEventType());
        }

        try {
            return invoker.invoke(parameterResolver.resolveEvent(event));
        } catch (Exception e) {
            throw new ConfigurationException("Failed to invoke handler for event: " + event.getEventType(), e);
        }
    }


    public void registerHandler(EventHandler eventHandler, Object bean, Method executeMethod) {
        Class<?> eventClass = parameterResolver.resolveEventType(executeMethod);

        ListenerCacheKey listenerCacheKey = new ListenerCacheKey(eventHandler.payloadKey(), eventHandler.eventType());
        if(handleListenerCache.containsKey(listenerCacheKey)){
            throw new ConfigurationException("EventHandler method duplicate, method:" + executeMethod + ", eventType:" + eventHandler.eventType() + ", listener Config:" + listenerCacheKey);
        }
        handleListenerCache.put(listenerCacheKey,new EventHandleInvoker(bean, executeMethod, parameterResolver));
    }
}