package com.milesight.iab.rule.flow;

import com.milesight.iab.rule.RuleEngineExecutor;
import com.milesight.iab.rule.constants.RuleNodeNames;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * @author leon
 */
public class CamelRuleEngineExecutor implements RuleEngineExecutor {

    private ProducerTemplate producerTemplate;

    public void initializeCamelContext(CamelContext camelContext) {
        this.producerTemplate = camelContext.createProducerTemplate();
    }

    @Override
    public Object exchangeUp(Object payload) {
       return producerTemplate.sendBody(RuleNodeNames.innerExchangeUpFlow, ExchangePattern.InOut, payload);
    }

    @Override
    public Object exchangeDown(Object payload) {
        return producerTemplate.sendBody(RuleNodeNames.innerExchangeDownFlow, ExchangePattern.InOut, payload);
    }

    @Override
    public void execute(String endpointUri, Object payload) {
        producerTemplate.sendBody(endpointUri, payload);
    }

    @Override
    public void execute(String endPointUri, Exchange exchange) {
        producerTemplate.send(endPointUri, exchange);
    }

    @Override
    public CompletableFuture<Exchange> asyncExecute(String endpointUri, Exchange exchange) {
        return producerTemplate.asyncSend(endpointUri, exchange);
    }

    @Override
    public CompletableFuture<Object> asyncExecute(String endpointUri, Object exchange) {
        return producerTemplate.asyncSendBody(endpointUri, exchange);
    }
}
