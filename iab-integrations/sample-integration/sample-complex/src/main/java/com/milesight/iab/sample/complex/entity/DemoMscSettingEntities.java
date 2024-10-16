package com.milesight.iab.sample.complex.entity;


import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.eventbus.api.IdentityKey;
import com.milesight.iab.context.integration.entity.annotation.Entity;
import com.milesight.iab.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;

/**
 * @author leon
 */
@IntegrationEntities
public class DemoMscSettingEntities extends ExchangePayload {

    @Entity(type = EntityType.PROPERTY, accessMod = AccessMod.RW)
    private String url;

    @Entity(type = EntityType.PROPERTY, accessMod = AccessMod.RW)
    private String accessKey;

    //服务类型
    @Entity(type = EntityType.PROPERTY, accessMod = AccessMod.RW)
    private String secretKey;

    @Override
    public String getKey() {
        return url;
    }

}
