package com.milesight.iab.entity.rule;

import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.EventContextAccessor;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.rule.annotations.RuleNode;
import com.milesight.iab.rule.api.PredicateNode;
import com.milesight.iab.rule.constants.RuleNodeNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Slf4j
@Component
@RuleNode(name = RuleNodeNames.innerExchangeValidator, description = "innerExchangeValidator")
public class GenericExchangeValidator implements PredicateNode<ExchangePayload> {

    @Autowired
    private EntityServiceProvider entityServiceProvider;
    @Override
    public boolean matches(ExchangePayload exchange){
        log.debug("Start ExchangeValidator matches, keys : {}", exchange.getKey());

        Map<String, Object> allPayloads = exchange.getAllPayloads();

        if(ObjectUtils.isEmpty(allPayloads)){
            log.warn("ExchangeValidator matches failed, allPayloads is empty");
            return false;
        }

        Map<String,Entity> entityMap = new HashMap<>();

        boolean isValid = allPayloads.keySet().stream().allMatch(k -> {
            Entity entity = entityServiceProvider.findByKey(k);
            entityMap.put(k, entity);
            return validateEntity(entity);
        });

        if(!isValid){
            return false;
        }

        exchange.putContext(EventContextAccessor.EXCHANGE_KEY_ENTITIES, entityMap);

        return true;
    }

    private boolean validateEntity(Entity entity) {

        if(entity == null){
            log.info("ExchangeValidator matches failed, entity is empty ");
            return false;
        }
        if(!entity.loadActiveIntegration().isPresent()){
            log.info("ExchangeValidator matches failed, activeIntegration is empty :{}", entity.getIntegrationId());
            return false;
        }
        if(StringUtils.hasText(entity.getDeviceKey()) && !entity.loadDevice().isPresent()){
            log.info("ExchangeValidator matches failed, device is empty : {}", entity.getDeviceKey());
            return false;
        }
        return true;
    }

}
