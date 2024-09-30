package com.milesight.iot.tmp.core.rule.api;

/**
 * @author leon
 */
public interface Processor<T> extends RuleConfigurationAware{

    public void processor(T exchange) ;
}
