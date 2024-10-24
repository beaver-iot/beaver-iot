package com.milesight.iab.tmp.rule.node;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.PredicateNode;
import com.milesight.iab.rule.constants.RuleNodeNames;
import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Slf4j
//@Component
@RuleNode(name = RuleNodeNames.innerExchangeValidator, description = "innerExchangeValidator")
public class ExchangeValidator implements PredicateNode<ExchangePayload> {

    @Override
    public boolean matches(ExchangePayload exchange){
        log.info("ExchangeValidator matches {}", exchange);
        return true;
    }

}
