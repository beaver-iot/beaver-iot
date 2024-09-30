package com.milesight.iot.tmp.core.rule.api;

/**
 * @author leon
 */
public interface Predicate<T> extends RuleConfigurationAware{

    public boolean matches(T exchange) ;

}
