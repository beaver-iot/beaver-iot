package com.milesight.beaveriot.sample.annotation.entity;


import com.milesight.beaveriot.context.integration.entity.annotation.Attribute;
import com.milesight.beaveriot.context.integration.entity.annotation.Entities;
import com.milesight.beaveriot.context.integration.entity.annotation.Entity;
import com.milesight.beaveriot.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.sample.annotation.enums.DeviceStatus;
import lombok.Data;

/**
 * 注解方式定义集成实体及子实体
 * @author leon
 */
@Data
@IntegrationEntities
public class DemoIntegrationEntities extends ExchangePayload {

    @Entity(type = EntityType.SERVICE, name = "mscEntitySync", identifier = "mscEntitySync", attributes = @Attribute(unit = "m", max = 1, min = 0, enumClass = DeviceStatus.class))
    private String entitySync;
    @Entity(type = EntityType.SERVICE)
    private String deviceSync;

    @Entity(type = EntityType.SERVICE)
    private DemoGroupSettingEntities connect;

    @Data
    @Entities
    public static class DemoGroupSettingEntities extends ExchangePayload {
        @Entity
        private String accessKey;
        @Entity
        private String secretKey;
    }
}
