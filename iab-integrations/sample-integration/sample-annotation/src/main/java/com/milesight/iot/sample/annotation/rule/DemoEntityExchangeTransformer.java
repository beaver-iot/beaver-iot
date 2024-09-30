package com.milesight.iot.sample.annotation.rule;

import com.milesight.iot.tmp.core.rule.api.RuleConfiguration;
import com.milesight.iot.tmp.core.rule.api.Transformer;
import com.milesight.iot.tmp.spec.entity.annotation.RuleNode;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;


/**
 * @author leon
 */
//默认node名称为类名首字母小写
@RuleNode(name = "demoEntityExchangeTransformer", description = "demo entity exchange transformer")
@Slf4j
public class DemoEntityExchangeTransformer implements Transformer<Exchange, ExchangePayload> {
    @Override
    public ExchangePayload transform(Exchange exchange) {
        log.info(exchange.toString());
        return new ExchangePayload();
    }

    @Override
    public RuleConfiguration getRuleConfiguration() {
        return null;
    }
}
