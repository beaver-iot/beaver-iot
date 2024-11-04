package com.milesight.beaveriot.eventbus;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.milesight.beaveriot.base.exception.EventBusExecutionException;
import com.milesight.beaveriot.eventbus.annotations.EventSubscribe;
import com.milesight.beaveriot.eventbus.api.Event;
import com.milesight.beaveriot.eventbus.api.EventResponse;
import com.milesight.beaveriot.eventbus.api.IdentityKey;
import com.milesight.beaveriot.eventbus.configuration.DisruptorOptions;
import com.milesight.beaveriot.eventbus.invoke.EventSubscribeInvoker;
import com.milesight.beaveriot.eventbus.invoke.ListenerParameterResolver;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author leon
 */
@Slf4j
public class DisruptorEventBus<T extends Event<? extends IdentityKey>> implements EventBus<T>, ApplicationContextAware {

    private final Map<Class<T>, Map<ListenerCacheKey,List<EventSubscribeInvoker<T>>>> asyncSubscribeCache = new ConcurrentHashMap<>();
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
            log.debug("disruptor is null, please subscribe first, event: {}" , message.getEventType());
            return;
        }

        disruptor.getRingBuffer().publishEvent((event, sequence) -> {
            if(message instanceof Copyable copyable){
                copyable.copy(message, event);
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

            Disruptor<Event<?>> disruptor = new Disruptor<>(() -> loadClass(target), disruptorOptions.getRingBufferSize(), executor);

            EventHandler[] eventHandlers = Arrays.stream(listener).map(ls -> (EventHandler<T>) (o, l, b) -> ls.accept(o)).toArray(EventHandler[]::new);

            disruptor.handleEventsWith(eventHandlers);

            disruptor.start();

            return disruptor;
        });
    }

    @Override
    public EventResponse handle(T event) {

        EventResponse eventResponse = new EventResponse();

        Map<ListenerCacheKey, List<EventSubscribeInvoker<T>>> listenerCacheKeyListMap = asyncSubscribeCache.get(event.getClass());

        if(listenerCacheKeyListMap == null){
            log.warn("no subscribe handler for event: {}", event.getEventType());
            return null;
        }

        for (Map.Entry<ListenerCacheKey, List<EventSubscribeInvoker<T>>> listenerCacheKeyListEntry : listenerCacheKeyListMap.entrySet()) {
            createSyncConsumer(listenerCacheKeyListEntry.getKey(), listenerCacheKeyListEntry.getValue(),eventResponse).accept(event);
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

        asyncSubscribeCache.computeIfAbsent(eventClass, k -> new ConcurrentHashMap<>());

        asyncSubscribeCache.get(eventClass).computeIfAbsent(listenerCacheKey, k -> new ArrayList<>()).add(new EventSubscribeInvoker<>(bean, executeMethod, parameterTypes, parameterResolver));
    }

    public void fireAsyncSubscribe(){
        asyncSubscribeCache.forEach((k, v) -> {
            List<Consumer<T>> allConsumers = v.entrySet().stream().map(entry -> createAsyncConsumer(entry.getKey(), entry.getValue())).toList();
            subscribe(k, allConsumers.toArray(new Consumer[0]));
        });
    }

    private Consumer<T> createSyncConsumer(ListenerCacheKey cacheKey, List<EventSubscribeInvoker<T>> invokers, @Nullable EventResponse eventResponses) {
        return event -> {
            String[] matchMultiKeys = filterMatchMultiKeys(event, cacheKey);
            if(ObjectUtils.isEmpty(matchMultiKeys)){
                return;
            }

            List<Throwable> causes = new ArrayList<>();
            invokers.forEach(invoker -> {
                try {
                    Object invoke = invoker.invoke(event, matchMultiKeys);
                    if(eventResponses != null && invoke instanceof EventResponse eventResponse){
                        eventResponses.putAll(eventResponse);
                    }
                } catch (Exception e) {
                    causes.add(e);
                    log.error("EventSubscribe method invoke error, method: {}" ,invoker, e);
                }
            });
            if(!CollectionUtils.isEmpty(causes)){
                throw new EventBusExecutionException("EventSubscribe method invoke error", causes);
            }
        };
    }

    private Consumer<T> createAsyncConsumer(ListenerCacheKey cacheKey, List<EventSubscribeInvoker<T>> invokers) {
        return event -> {
            String[] matchMultiKeys = filterMatchMultiKeys(event, cacheKey);
            if(ObjectUtils.isEmpty(matchMultiKeys)){
                return;
            }
            invokers.forEach(invoker -> {
                try {
                    invoker.invoke(event, matchMultiKeys);
                } catch (Exception e) {
                    log.error("EventSubscribe method invoke error, method: {}" ,invoker, e);
                }
            });
        };
    }

    private String[] filterMatchMultiKeys(T event, ListenerCacheKey cacheKey) {
        if(!cacheKey.matchEventType(event.getEventType())){
            return new String[0];
        }
        return cacheKey.matchMultiKeys(event.getPayloadKey());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private <E> E loadClass(Class<E> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new EventBusExecutionException("Class load exception",e);
        }
    }
}
