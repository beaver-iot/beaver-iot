package com.milesight.iab.tmp.rule.node;

import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.PredicateNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author leon
 */
@Slf4j
//@Component
@RuleNode(name = "innerCustomExchangeDownHandlerPredicate", description = "CustomExchangeDownHandler")
public class CustomExchangeDownHandlerPredicate implements PredicateNode<ExchangePayload> {

    @Override
    public boolean matches(ExchangePayload exchange) {
        log.info("CustomExchangeDownHandler Predicate {}",exchange.toString());
        return new Random().nextInt(2) == 0;
    }

}
