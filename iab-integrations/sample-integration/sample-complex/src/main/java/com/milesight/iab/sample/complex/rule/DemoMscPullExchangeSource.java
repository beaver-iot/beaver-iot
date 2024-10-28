package com.milesight.iab.sample.complex.rule;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.ProcessorNode;
import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = "demoMscPullExchangeSource")
public class DemoMscPullExchangeSource implements ProcessorNode<Exchange> {

    @Override
    public void processor(Exchange exchange) {
        Map<String,Object> allPayloads = new HashMap<>();
        allPayloads.put("demo-complex-integration.integration.setting.url", "123");
        allPayloads.put("demo-complex-integration.integration.setting.access_key", "456");
        ExchangePayload exchangePayload = ExchangePayload.create(allPayloads);
        exchange.getIn().setBody(exchangePayload);
        log.info("==============timer call demoMscPullExchangeSource, size:{}==============",exchangePayload.size());
    }

}
