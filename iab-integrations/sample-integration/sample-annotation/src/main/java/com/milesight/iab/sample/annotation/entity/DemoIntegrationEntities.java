package com.milesight.iab.sample.annotation.entity;


import com.milesight.iab.context.integration.entity.annotation.*;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.sample.annotation.enums.DeviceStatus;
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

    @Entity(type = EntityType.SERVICE, syncCall = false)
    private DemoGroupSettingEntities connect;

    @Data
    @Entities
    public static class DemoGroupSettingEntities {
        @Entity
        private String accessKey;
        @Entity
        private String secretKey;
    }
}
