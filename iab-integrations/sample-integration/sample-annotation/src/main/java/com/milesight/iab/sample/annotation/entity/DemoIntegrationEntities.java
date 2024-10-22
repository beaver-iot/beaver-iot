package com.milesight.iab.sample.annotation.entity;


import com.milesight.iab.context.integration.entity.annotation.Entity;
import com.milesight.iab.context.integration.entity.annotation.Entities;
import com.milesight.iab.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.iab.context.integration.entity.annotation.KeyValue;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.Data;

/**
 * 注解方式定义集成实体及子实体
 * @author leon
 */
@Data
@IntegrationEntities
public class DemoIntegrationEntities extends ExchangePayload {

    @Entity(type = EntityType.SERVICE, name = "mscEntitySync", identifier = "mscEntitySync", attributes = {@KeyValue(key = "key1", value = "value1")})
    private String entitySync;
    @Entity(type = EntityType.SERVICE)
    private String deviceSync;

    @Entity(type = EntityType.SERVICE, syncCall = false)
    private DemoGroupSettingEntities connect;

    @Entities
    public class DemoGroupSettingEntities {
        @Entity
        private String accessKey;
        @Entity
        private String secretKey;
    }
}
