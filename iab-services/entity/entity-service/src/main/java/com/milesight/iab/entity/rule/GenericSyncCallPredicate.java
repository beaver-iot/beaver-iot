package com.milesight.iab.entity.rule;

import com.milesight.iab.context.integration.model.EventContextAccessor;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.PredicateNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = "innerSyncCallPredicate", description = "SyncCallPredicate")
public class GenericSyncCallPredicate implements PredicateNode<ExchangePayload> {

    @Override
    public boolean matches(ExchangePayload exchange) {

        Boolean syncCall = exchange.getContext(EventContextAccessor.EXCHANGE_KEY_SYNC_CALL, false);

        log.debug("SyncCallPredicate Predicate {}",syncCall);

        return syncCall;
    }

}
