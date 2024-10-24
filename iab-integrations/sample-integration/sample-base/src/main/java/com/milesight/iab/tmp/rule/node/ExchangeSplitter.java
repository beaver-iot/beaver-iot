package com.milesight.iab.tmp.rule.node;

import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.SplitterNode;
import com.milesight.iab.rule.constants.RuleNodeNames;
import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Slf4j
//@Component
@RuleNode(name = RuleNodeNames.innerExchangeSplitter, description = "innerExchangeSplitter")
public class ExchangeSplitter implements SplitterNode<ExchangePayload> {

    @Override
    public List<ExchangePayload> split(ExchangePayload exchangePayload) {
        log.info("ExchangeSplitter split {}",exchangePayload.toString());
        ArrayList<ExchangePayload> splitPayload = new ArrayList<>();
        Map<String, Object> allPayloads = exchangePayload.getAllPayloads();
        allPayloads.forEach((k, v) -> {
            ExchangePayload newPayload = ExchangePayload.create(k, v);
            splitPayload.add(newPayload);
        } );

        return splitPayload;
    }
}
