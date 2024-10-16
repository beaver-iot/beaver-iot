package com.milesight.iab.sample.annotation.rule;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.RuleConfiguration;
import com.milesight.iab.rule.api.TransformerNode;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

/**
 * @author leon
 */
//默认node名称为类名首字母小写
@RuleNode(name = "annotationDemoTransformer", description = "demo entity exchange transformer")
@Slf4j
public class DemoEntityExchangeTransformer implements TransformerNode<Exchange, ExchangePayload> {
    @Override
    public ExchangePayload transform(Exchange exchange) {
        log.info(exchange.toString());
        return new ExchangePayload();
    }

    @Override
    public RuleConfiguration getRuleConfiguration() {
        //todo : 后续的规则引擎
        return null;
    }
}
