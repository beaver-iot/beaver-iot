package com.milesight.beaveriot.rule.api;

/**
 * @author leon
 */
public interface ProcessorNode<T> extends RuleConfigurationAware{

    void processor(T exchange) ;
}
