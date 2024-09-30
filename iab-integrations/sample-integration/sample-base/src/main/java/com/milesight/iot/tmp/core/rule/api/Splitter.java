package com.milesight.iot.tmp.core.rule.api;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.Route;

import java.util.concurrent.ExecutorService;

/**
 * @author leon
 */
public class Splitter extends org.apache.camel.processor.Splitter implements RuleConfigurationAware{
    public Splitter(CamelContext camelContext, Route route, Expression expression, Processor destination, AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService, boolean shutdownExecutorService, boolean streaming, boolean stopOnException, long timeout, Processor onPrepare, boolean useSubUnitOfWork, boolean parallelAggregate) {
        super(camelContext, route, expression, destination, aggregationStrategy, parallelProcessing, executorService, shutdownExecutorService, streaming, stopOnException, timeout, onPrepare, useSubUnitOfWork, parallelAggregate);
    }

    public Splitter(CamelContext camelContext, Route route, Expression expression, Processor destination, AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService, boolean shutdownExecutorService, boolean streaming, boolean stopOnException, long timeout, Processor onPrepare, boolean useSubUnitOfWork, boolean parallelAggregate, String delimiter) {
        super(camelContext, route, expression, destination, aggregationStrategy, parallelProcessing, executorService, shutdownExecutorService, streaming, stopOnException, timeout, onPrepare, useSubUnitOfWork, parallelAggregate, delimiter);
    }


}
