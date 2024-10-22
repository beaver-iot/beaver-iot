package com.milesight.iab.eventbus.subscribe;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.eventbus.ListenerCacheKey;
import com.milesight.iab.eventbus.ListenerParameterResolver;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;
import com.milesight.iab.eventbus.configuration.DisruptorOptions;
import com.milesight.iab.eventbus.handler.EventHandlerDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author leon
 */
@Slf4j
public class DisruptorEventBus<T extends Event<? extends IdentityKey>> implements EventBus<T> {

    private final Map<Class<T>, Map<ListenerCacheKey,List<EventSubscribeInvoker>>> subscriberCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, Disruptor<Event<?>>> disruptorCache = new ConcurrentHashMap<>();

    private DisruptorOptions disruptorOptions;

    private EventHandlerDispatcher eventHandlerDispatcher;

    private ListenerParameterResolver parameterResolver;

    public DisruptorEventBus(DisruptorOptions disruptorOptions,EventHandlerDispatcher eventHandlerDispatcher, ListenerParameterResolver parameterResolver) {
        this.disruptorOptions  = disruptorOptions;
        this.eventHandlerDispatcher = eventHandlerDispatcher;
        this.parameterResolver = parameterResolver;
    }

    @Override
    public void publish(T message) {
        Disruptor<Event<?>> disruptor = disruptorCache.get(message.getClass());

        Assert.notNull(disruptor, "disruptor is null, please subscribe first");

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
    public Object handle(T message) {
        return eventHandlerDispatcher.handle(message);
    }

    @Override
    public void shutdown() {
        disruptorCache.values().forEach(Disruptor::shutdown);
    }

    @Override
    public void subscribe(Class<T> target, Consumer<T>... listener) {

        ExecutorService executor = new ThreadPoolExecutor(0, disruptorOptions.getMaxPoolSize(), 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), DaemonThreadFactory.INSTANCE);

        subscribe(target, executor, listener);
    }

    private <E> E loadClass(Class<E> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Class load exception",e);
        }
    }

    public void registerSubscriber(EventSubscribe eventSubscribe, Object bean, Method executeMethod){

        Class<T> eventClass = parameterResolver.resolveEventType(executeMethod);

        ListenerCacheKey listenerCacheKey = new ListenerCacheKey(eventSubscribe.payloadKeyExpression(), eventSubscribe.eventType());

        log.debug("registerSubscriber: {}, subscriber expression: {}" , executeMethod, listenerCacheKey);

        subscriberCache.computeIfAbsent(eventClass, k -> {
            Map<ListenerCacheKey, List<EventSubscribeInvoker>> invokerMap = new ConcurrentHashMap<>();
            return invokerMap;
        });

        subscriberCache.get(eventClass).computeIfAbsent(listenerCacheKey, k -> new ArrayList<>()).add(new EventSubscribeInvoker(bean, executeMethod, listenerCacheKey,parameterResolver));
    }

    public void fireSubscriber(){
        subscriberCache.forEach((k,v) -> {
            List<Consumer<T>> allConsumers = v.entrySet().stream().map(entry -> {
                ListenerCacheKey cacheKey = entry.getKey();
                List<EventSubscribeInvoker> invokers = entry.getValue();
                return (Consumer<T>) o -> {
                    if(!cacheKey.expressionMatch(o.getPayloadKey(),o.getEventType())){
                       return;
                    }
                    invokers.forEach(invoker -> {
                        try {
                            invoker.invoke(o);
                        } catch (Exception e) {
                            log.error("EventSubscribe method invoke error, method: {}" ,invoker, e);
                        }
                    });
                };
            }).toList();

            subscribe(k, allConsumers.toArray(new Consumer[0]));
        });
    }

}
