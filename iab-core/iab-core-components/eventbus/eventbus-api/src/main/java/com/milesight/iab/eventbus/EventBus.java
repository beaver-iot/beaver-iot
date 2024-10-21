package com.milesight.iab.eventbus;

import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author leon
 */
public interface EventBus<T extends Event<? extends IdentityKey>> {

    void publish(T message);

    void subscribe(Class<T> target, Consumer<T> listener);

    void subscribe(Class<T> target, Consumer<T> listener, Executor executor);

    default void shutdown(){
    }

}
