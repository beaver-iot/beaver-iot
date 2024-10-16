package com.milesight.iab.rule.api;

import java.util.List;

/**
 * @author leon
 */
public interface SplitterNode<T> extends RuleConfigurationAware{

    List<T> split(T payload) ;

}
