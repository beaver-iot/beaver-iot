package com.milesight.iab.rule.api;

/**
 * @author leon
 */
public interface SinkNode<T> extends RuleConfigurationAware{

    void processor(T exchange) ;
}
