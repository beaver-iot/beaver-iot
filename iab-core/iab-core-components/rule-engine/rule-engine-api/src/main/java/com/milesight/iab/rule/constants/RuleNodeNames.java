package com.milesight.iab.rule.constants;

/**
 * @author leon
 */
public interface RuleNodeNames {

    String innerExchangeDownFlow = "direct:innerExchangeDownFlow";
    String innerExchangeUpFlow = "direct:innerExchangeUpFlow";
    String innerExchangeDownHandler = "direct:innerExchangeDownHandler";
    String innerExchangeValidator = "innerExchangeValidator";
    String innerExchangeSplitter = "innerExchangeSplitter";
    String innerSyncCallPredicate = "innerSyncCallPredicate";
    String innerCustomExchangeDownHandlerPredicate = "innerCustomExchangeDownHandlerPredicate";
    String innerLatestExchangeSaveAction = "innerLatestExchangeSaveAction";
    String innerHistoryExchangeSaveAction = "innerHistoryExchangeSaveAction";
    String innerEventHandlerAction = "innerEventHandlerAction";
    String innerEventSubscribeAction = "innerEventSubscribeAction";

}
