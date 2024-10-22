package com.milesight.iab.eventbus.subscribe;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;
import com.milesight.iab.eventbus.configuration.DisruptorOptions;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author leon
 */
public class DisruptorEventBus<T extends Event<? extends IdentityKey>> implements EventBus<T> {

    private final Map<Class<?>, Disruptor<Event<?>>> disruptorCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<?>, Executor> executorCache = new ConcurrentHashMap<>();

    private DisruptorOptions disruptorOptions;

    private static DisruptorEventBus INSTANCE = null;

    private DisruptorEventBus(DisruptorOptions disruptorOptions) {
        this.disruptorOptions  = disruptorOptions;
    }

    public static EventBus getInstance(){
        return getInstance(null);
    }

    public static EventBus getInstance(DisruptorOptions disruptorOptions) {
        if(INSTANCE == null){
            synchronized (DisruptorEventBus.class){
                if(INSTANCE == null){
                    DisruptorOptions options = disruptorOptions == null ? DisruptorOptions.defaultOptions() : disruptorOptions;
                    INSTANCE = new DisruptorEventBus(options);
                }
            }
        }
        return INSTANCE;
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
                //todo
                event.setPayload(null);
            }
        });
    }

    @Override
    public void subscribe(Class<T> target, Consumer<T> listener, Executor executor){

        disruptorCache.computeIfAbsent(target, k -> {

            Disruptor<Event<?>> disruptor = new Disruptor(() -> loadClass(target), disruptorOptions.getRingBufferSize(), executor);

            disruptor.handleEventsWith((event, sequence, endOfBatch) -> listener.accept((T) event));

            disruptor.start();

            executorCache.put(target, executor);

            return disruptor;
        });
    }

    @Override
    public void subscribe(Class<T> target, Consumer<T> listener) {

        ExecutorService executor = new ThreadPoolExecutor(0, disruptorOptions.getMaxPoolSize(), 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), DaemonThreadFactory.INSTANCE);

        subscribe(target, listener, executor);
    }

    @Override
    public void shutdown() {

        disruptorCache.values().forEach(Disruptor::shutdown);

//        executorCache.values().forEach(ExecutorService::shutdown);
    }

    private <E> E loadClass(Class<E> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Class load exception",e);
        }
    }

    public DisruptorOptions getDisruptorOptions() {
        return disruptorOptions;
    }
}
