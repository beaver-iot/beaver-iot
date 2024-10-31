package com.milesight.beaveriot.sample.complex.entity;


import com.milesight.beaveriot.context.integration.entity.annotation.Entities;
import com.milesight.beaveriot.context.integration.entity.annotation.Entity;
import com.milesight.beaveriot.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.Data;

/**
 * @author leon
 */
@Data
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

    @Data
    @Entities
    public static class DemoMscSettingEntities extends ExchangePayload {

        @Entity
        private String url;

        @Entity
        private String accessKey;

        @Entity
        private String secretKey;

    }

}
