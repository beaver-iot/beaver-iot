package com.milesight.iab.eventbus;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.milesight.iab.eventbus.invoke.EventSubscribeInvoker;
import com.milesight.iab.eventbus.invoke.ListenerParameterResolver;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.EventResponse;
import com.milesight.iab.eventbus.api.IdentityKey;
import com.milesight.iab.eventbus.configuration.DisruptorOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author leon
 */
@Slf4j
public class DisruptorEventBus<T extends Event<? extends IdentityKey>> implements EventBus<T>, ApplicationContextAware {

    private Map<ListenerCacheKey, List<EventSubscribeInvoker>> syncSubscribeCache = new ConcurrentHashMap<>();
    private final Map<Class<T>, Map<ListenerCacheKey,List<EventSubscribeInvoker>>> asyncSubscribeCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, Disruptor<Event<?>>> disruptorCache = new ConcurrentHashMap<>();
    private DisruptorOptions disruptorOptions;
    private ListenerParameterResolver parameterResolver;
    private ApplicationContext applicationContext;

    public DisruptorEventBus(DisruptorOptions disruptorOptions, ListenerParameterResolver parameterResolver) {
        this.disruptorOptions  = disruptorOptions;
        this.parameterResolver = parameterResolver;
    }

    @Override
    public void publish(T message) {
        Disruptor<Event<?>> disruptor = disruptorCache.get(message.getClass());

        if(disruptor == null){
            log.warn("disruptor is null, please subscribe first");
            return;
        }

        disruptor.getRingBuffer().publishEvent((event, sequence) -> {
            if(message instanceof Copyable){
                ((Copyable) message).copy(message, event);
            }else{
                IdentityKey payload = message.getPayload();
                event.setPayload(payload);
                event.setEventType(message.getEventType());
            }
        });
    }

    @Override
    public void subscribe(Class<T> target, Executor executor, Consumer<T>... listener){

        disruptorCache.computeIfAbsent(target, k -> {

            Disruptor<Event<?>> disruptor = new Disruptor(() -> loadClass(target), disruptorOptions.getRingBufferSize(), executor);

            EventHandler[] eventHandlers = Arrays.stream(listener).map(ls -> (EventHandler<T>) (o, l, b) -> ls.accept(o)).toArray(EventHandler[]::new);

            disruptor.handleEventsWith(eventHandlers);

            disruptor.start();

            return disruptor;
        });
    }

    @Override
    public Object handle(T event) {

        Map<String, Object> eventResponse = new HashMap<>();

        for (Map.Entry<ListenerCacheKey, List<EventSubscribeInvoker>> listenerCacheKeyListEntry : syncSubscribeCache.entrySet()) {
            createConsumer(listenerCacheKeyListEntry.getKey(), listenerCacheKeyListEntry.getValue(),eventResponse).accept(event);
        }

        return eventResponse;
    }

    @Override
    public void shutdown() {
        disruptorCache.values().forEach(Disruptor::shutdown);
    }

    @Override
    public void subscribe(Class<T> target, Consumer<T>... listener) {

        Executor executor = (Executor) applicationContext.getBean(disruptorOptions.getEventBusTaskExecutor());

        subscribe(target, executor, listener);
    }

    public void registerAsyncSubscribe(EventSubscribe eventSubscribe, Object bean, Method executeMethod){

        Class<?> parameterTypes = parameterResolver.resolveParameterTypes(executeMethod);

        Class<T> eventClass = parameterResolver.resolveActualEventType(executeMethod);

        ListenerCacheKey listenerCacheKey = new ListenerCacheKey(eventSubscribe.payloadKeyExpression(), eventSubscribe.eventType());

        log.debug("registerAsyncSubscribe: {}, subscriber expression: {}" , executeMethod, listenerCacheKey);

        asyncSubscribeCache.computeIfAbsent(eventClass, k -> {
            Map<ListenerCacheKey, List<EventSubscribeInvoker>> invokerMap = new ConcurrentHashMap<>();
            return invokerMap;
        });

        asyncSubscribeCache.get(eventClass).computeIfAbsent(listenerCacheKey, k -> new ArrayList<>()).add(new EventSubscribeInvoker(bean, executeMethod, parameterTypes, parameterResolver));
    }

    public void registerSyncSubscribe(EventSubscribe eventSubscribe, Object bean, Method executeMethod){

        Class<?> parameterTypes = parameterResolver.resolveParameterTypes(executeMethod);

        ListenerCacheKey listenerCacheKey = new ListenerCacheKey(eventSubscribe.payloadKeyExpression(), eventSubscribe.eventType());

        log.debug("registerSyncSubscribe: {}, subscriber expression: {}" , executeMethod, listenerCacheKey);

        syncSubscribeCache.computeIfAbsent(listenerCacheKey, k -> new ArrayList<>()).add(new EventSubscribeInvoker(bean, executeMethod, parameterTypes, parameterResolver));
    }

    public void fireAsyncSubscribe(){
        asyncSubscribeCache.forEach((k, v) -> {
            List<Consumer<T>> allConsumers = v.entrySet().stream().map(entry -> createConsumer(entry.getKey(), entry.getValue(), Map.of())).toList();
            subscribe(k, allConsumers.toArray(new Consumer[0]));
        });
    }

    private Consumer<T> createConsumer(ListenerCacheKey cacheKey, List<EventSubscribeInvoker> invokers, Map<String,Object> eventResponses) {
        return event -> {
            if(!cacheKey.matchEventType(event.getEventType())){
                return;
            }
            String[] matchMultiKeys = cacheKey.matchMultiKeys(event.getPayloadKey());
            if(ObjectUtils.isEmpty(matchMultiKeys)){
                return;
            }
            invokers.forEach(invoker -> {
                try {
                    Object invoke = invoker.invoke(event, matchMultiKeys);
                    if(invoke != null && invoke instanceof EventResponse){
                        EventResponse eventResponse = (EventResponse) invoke;
                        if(eventResponse != null){
                            eventResponses.put(eventResponse.getKey(), eventResponse.getValue());
                        }
                    }
                } catch (Exception e) {
                    log.error("EventSubscribe method invoke error, method: {}" ,invoker, e);
                }
            });
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private <E> E loadClass(Class<E> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Class load exception",e);
        }
    }
}
