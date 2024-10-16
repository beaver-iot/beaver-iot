package com.milesight.iab.rule.api;

/**
 * @author leon
 */
public interface TransformerNode<S,T> extends RuleConfigurationAware{

    T transform(S exchange) ;
}
