package com.milesight.iot.sample.annotation.entity;


import com.milesight.iot.sample.annotation.enums.DevcieStatus;
import com.milesight.iot.tmp.spec.entity.annotation.DeviceEntities;
import com.milesight.iot.tmp.spec.entity.annotation.DeviceEntity;
import com.milesight.iot.tmp.spec.entity.annotation.KeyValue;
import com.milesight.iot.tmp.spec.entity.enums.AccessMod;
import com.milesight.iot.tmp.spec.entity.enums.Type;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;

/**
 * @author leon
 */
@DeviceEntities(name="demoDevice", additional = {@KeyValue(key = "sn", value = "demoSN")}, key="${integration.name}.device.demoSN")
public class DemoDeviceEntities extends ExchangePayload {

    //指定的申明配置
    @DeviceEntity(name = "temperature", key = "${deviceKey}.temperature", accessMod = AccessMod.READ_WRITE, type = Type.PROPERTY)
    private Double temperature;

    //默认device key为： ${deviceKey}.${fieldName}
    //默认device name为： ${fieldName}
    @DeviceEntity
    private String humidity ;

    //枚举类型
    @DeviceEntity
    private DevcieStatus status;

    //服务类型
    @DeviceEntity(type= Type.SERVICE)
    private String changeStatus;

}
