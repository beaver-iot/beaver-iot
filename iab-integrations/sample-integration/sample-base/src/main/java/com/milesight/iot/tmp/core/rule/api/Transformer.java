package com.milesight.iot.tmp.core.rule.api;

/**
 * @author leon
 */
public interface Transformer <S,T> extends RuleConfigurationAware{

    public T transform(S exchange) ;
}
