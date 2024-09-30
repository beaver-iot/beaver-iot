package com.milesight.iot.sample.complex.rule;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.processor.Splitter;

import java.util.concurrent.ExecutorService;

/**
 * @author leon
 */
@Slf4j
public class DemoEntityExchangeSplitter extends Splitter {


    public DemoEntityExchangeSplitter(CamelContext camelContext, Route route, Expression expression, Processor destination, AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService, boolean shutdownExecutorService, boolean streaming, boolean stopOnException, long timeout, Processor onPrepare, boolean useSubUnitOfWork, boolean parallelAggregate) {
        super(camelContext, route, expression, destination, aggregationStrategy, parallelProcessing, executorService, shutdownExecutorService, streaming, stopOnException, timeout, onPrepare, useSubUnitOfWork, parallelAggregate);
    }

    public DemoEntityExchangeSplitter(CamelContext camelContext, Route route, Expression expression, Processor destination, AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService, boolean shutdownExecutorService, boolean streaming, boolean stopOnException, long timeout, Processor onPrepare, boolean useSubUnitOfWork, boolean parallelAggregate, String delimiter) {
        super(camelContext, route, expression, destination, aggregationStrategy, parallelProcessing, executorService, shutdownExecutorService, streaming, stopOnException, timeout, onPrepare, useSubUnitOfWork, parallelAggregate, delimiter);
    }
}
