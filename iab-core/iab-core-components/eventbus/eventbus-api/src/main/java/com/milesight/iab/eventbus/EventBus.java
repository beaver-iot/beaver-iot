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

    void publish(T var1);

    CompletableFuture<Long> publishAsync(T var1);

    void subscribe(Class<T> var1, Consumer<T> var2);

    void subscribe(Class<T> var1, Consumer<T> var2, Executor var3);

    default void shutdown() {
    }

}
