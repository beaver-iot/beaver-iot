package com.milesight.iab.sample.complex.entity;


import com.milesight.iab.context.integration.entity.annotation.Entities;
import com.milesight.iab.context.integration.entity.annotation.Entity;
import com.milesight.iab.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.ExchangePayloadAccessor;

/**
 * @author leon
 */
@IntegrationEntities
public class DemoMscServiceEntities extends ExchangePayload {

    // business logic
    @Entity(type = EntityType.SERVICE)
    private String connect;

    @Entity(type = EntityType.SERVICE)
    private String syncDevice;

    @Entity(type = EntityType.SERVICE)
    private String syncHistory;

    @Entity(type = EntityType.SERVICE)
    private DemoMscSettingEntities setting;

    @Entities
    public class DemoMscSettingEntities extends ExchangePayload {

        @Entity
        private String url;

        @Entity
        private String accessKey;

        @Entity
        private String secretKey;

    }

}
