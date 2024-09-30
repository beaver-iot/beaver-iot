package com.milesight.iot.sample.annotation.entity;


import com.milesight.iot.tmp.spec.entity.annotation.DeviceEntity;
import com.milesight.iot.tmp.spec.entity.annotation.IntegrationEntities;
import com.milesight.iot.tmp.spec.entity.enums.AccessMod;
import com.milesight.iot.tmp.spec.entity.enums.Type;

/**
 * @author leon
 */
@IntegrationEntities
public class DemoIntegrationEntities {

    //指定的申明配置
    @DeviceEntity(name = "accessKey", key = "${integration.name}.integration.accessKey", accessMod = AccessMod.READ_WRITE, type = Type.PROPERTY)
    private String accessKey;

    //默认integration key为： integration.${integration.name}.${fieldName}  ---如果驼峰的将转换成.
    //默认integration name为： ${fieldName}
    @DeviceEntity
    private String url;

    //服务类型
    @DeviceEntity(type= Type.SERVICE)
    private String connect;

    @DeviceEntity(key = "${integration.name}.integration.connect", accessMod = AccessMod.READ_WRITE, type = Type.SERVICE)
    private DemoGroupIntegrationEntities entities;
}
