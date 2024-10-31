package com.milesight.beaveriot.entity.rule;

import com.milesight.beaveriot.context.api.EntityServiceProvider;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.rule.annotations.RuleNode;
import com.milesight.beaveriot.rule.api.ProcessorNode;
import com.milesight.beaveriot.rule.constants.RuleNodeNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @author leon
 */

@Slf4j
@Component
@RuleNode(name = RuleNodeNames.innerExchangeSaveAction, description = "innerExchangeSaveAction")
public class GenericExchangeSaveAction implements ProcessorNode<ExchangePayload> {

    @Autowired
    private EntityServiceProvider entityServiceProvider;

    @Override
    public void processor(ExchangePayload exchange) {
        log.info("HistoryExchangeSaveAction processor {}", exchange.toString());

        // Save event entities， only save history
        Map<String, Object> eventEntities = exchange.getPayloadsByEntityType(EntityType.EVENT);
        if (!ObjectUtils.isEmpty(eventEntities)) {
            entityServiceProvider.saveExchangeHistory(ExchangePayload.create(eventEntities, exchange.getContext()));
        }

        // Save property entities
        Map<String, Object> propertyEntities = exchange.getPayloadsByEntityType(EntityType.PROPERTY);
        if (!ObjectUtils.isEmpty(propertyEntities)) {
            ExchangePayload exchangePayload = ExchangePayload.create(propertyEntities, exchange.getContext());
            entityServiceProvider.saveExchange(exchangePayload);
            entityServiceProvider.saveExchangeHistory(exchangePayload);
        }

        // Save service entities， only save history
        Map<String, Object> serviceEntities = exchange.getPayloadsByEntityType(EntityType.SERVICE);
        if (!ObjectUtils.isEmpty(serviceEntities)) {
            entityServiceProvider.saveExchangeHistory(ExchangePayload.create(serviceEntities, exchange.getContext()));
        }

    }
}
