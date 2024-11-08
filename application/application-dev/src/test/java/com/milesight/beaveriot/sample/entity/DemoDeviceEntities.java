package com.milesight.beaveriot.sample.entity;


import com.milesight.beaveriot.context.integration.entity.annotation.Attribute;
import com.milesight.beaveriot.context.integration.entity.annotation.DeviceTemplateEntities;
import com.milesight.beaveriot.context.integration.entity.annotation.Entity;
import com.milesight.beaveriot.context.integration.enums.AccessMod;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.integration.enums.EntityValueType;
import com.milesight.beaveriot.context.integration.model.Device;
import com.milesight.beaveriot.context.integration.model.DeviceBuilder;
import com.milesight.beaveriot.context.integration.model.EntityBuilder;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.Data;


/**
 * 注解方式定义设备实体
 *
 * @author leon
 */
@Data
@DeviceTemplateEntities(name="demoDevice")
//@DeviceEntities(name="demoDevice", additional = {@KeyValue(key = "sn", value = "demoSN")}, identifier = "demoSN")
public class DemoDeviceEntities extends ExchangePayload {
    //    msc-integration.device.demoSN.temperature
//    msc-integration.device.*.temperature
//    设备Key（Device Key）：{集成名}.device.{设备唯一标识符号,如SN}
//    设备实体Key（Device Entity Key）：{集成名}.device.{设备唯一标识符号,如SN}.{实体唯一标识符，设备属性/服务/事件}
//    集成实体Key（Integration Entity Key）:{集成名}.integration.{实体唯一标识符，如属性/服务/事件}
    //指定的申明配置
    @Entity(name = "temperature", identifier = "temperature", accessMod = AccessMod.RW, type = EntityType.PROPERTY)
    public Double temperature;

    @Entity
    private String humidity;

    //枚举类型
    @Entity(attributes = {@Attribute(unit = "ms", min = 0, max = 1000)})
    private Integer status;

    //服务类型
    @Entity(type= EntityType.SERVICE)
    private String changeStatus;

    public static void main(String[] args) {
        com.milesight.beaveriot.context.integration.model.Entity entityConfig = new EntityBuilder()
                .property("prop_parent", AccessMod.W)
                .valueType(EntityValueType.STRING)
                .children()
                .valueType(EntityValueType.STRING).property("prop_children1", AccessMod.W).end()
                .children()
                .valueType(EntityValueType.STRING).property("prop_children2", AccessMod.W).end()
                .build();
        Device device = new DeviceBuilder("myid")
                .name("complexDevice1")
                .identifier("complexDevice1")
                .entity(entityConfig)
                .build();
        System.out.println(device);
    }
}
