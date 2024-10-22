package com.milesight.iab.rule;

import org.apache.camel.Exchange;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author leon
 */
public interface RuleEngineExecutor {

    Object exchangeUp(Object payload);

    Object exchangeDown(Object payload);

    void execute(String endpointUri, Object payload);

    void execute(String endPointUri, Exchange exchange);

    CompletableFuture<Exchange> asyncExecute(String endpointUri, Exchange exchange);

    CompletableFuture<Object> asyncExecute(String endpointUri, Object exchange);
}
