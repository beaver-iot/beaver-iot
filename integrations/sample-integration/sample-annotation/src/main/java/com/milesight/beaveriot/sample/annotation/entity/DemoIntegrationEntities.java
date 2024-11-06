package com.milesight.beaveriot.sample.annotation.entity;


import com.milesight.beaveriot.context.integration.entity.annotation.Attribute;
import com.milesight.beaveriot.context.integration.entity.annotation.Entities;
import com.milesight.beaveriot.context.integration.entity.annotation.Entity;
import com.milesight.beaveriot.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.beaveriot.context.integration.enums.AccessMod;
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

    // Service Samples
    @Entity(type = EntityType.SERVICE, name = "mscEntitySync", identifier = "mscEntitySync", attributes = @Attribute(unit = "m", max = 1, min = 0, enumClass = DeviceStatus.class))
    private String entitySync;
    @Entity(type = EntityType.SERVICE)
    private String deviceSync;
    @Entity(type = EntityType.SERVICE)
    private Boolean boolService;

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

    // Property Samples
    @Entity(type = EntityType.PROPERTY, name="Property Read Only", accessMod = AccessMod.R)
    private String propertyReadOnly;

    @Entity(type = EntityType.PROPERTY, name="Property Read Write", accessMod = AccessMod.RW)
    private String propertyReadWrite;

    @Entity(type = EntityType.PROPERTY, name="Property Read Only Enum", attributes = @Attribute(enumClass = DeviceStatus.class), accessMod = AccessMod.R)
    private String propertyReadOnlyEnum;

    @Entity(type = EntityType.PROPERTY, name="Property Read Write Enum", attributes = @Attribute(enumClass = DeviceStatus.class), accessMod = AccessMod.RW)
    private String propertyReadWriteEnum;

    @Entity(type = EntityType.PROPERTY, name="Boolean Read Write", accessMod = AccessMod.RW)
    private Boolean propertyBooleanReadWrite;

    @Entity(type = EntityType.PROPERTY, name="Boolean Read Only", accessMod = AccessMod.RW)
    private Boolean propertyBooleanReadOnly;

    @Entity(type = EntityType.PROPERTY, name="Property Group", accessMod = AccessMod.RW)
    private SamplePropertyEntities propertyGroup;

    @Entity(type = EntityType.PROPERTY, name="Property Group Read Only", accessMod = AccessMod.R)
    private SamplePropertyEntities propertyGroupReadonly;

    // Property Samples
    @Entity(type = EntityType.EVENT, name="Event")
    private String eventStringSample;

    @Data
    @Entities
    public static class SamplePropertyEntities extends ExchangePayload {
        @Entity
        private String prop1;
        @Entity
        private String prop2;
    }
}
