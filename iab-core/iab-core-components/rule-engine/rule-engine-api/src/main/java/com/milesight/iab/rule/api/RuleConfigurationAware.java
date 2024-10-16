package com.milesight.iab.rule.api;

/**
 * @author leon
 */
public interface RuleConfigurationAware {

    default RuleConfiguration getRuleConfiguration(){
        return null;
    }
}
