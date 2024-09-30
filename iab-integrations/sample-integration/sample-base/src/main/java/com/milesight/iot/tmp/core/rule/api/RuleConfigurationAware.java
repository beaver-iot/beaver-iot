package com.milesight.iot.tmp.core.rule.api;

/**
 * @author leon
 */
public interface RuleConfigurationAware {

    default RuleConfiguration getRuleConfiguration(){
        return null;
    }
}
