package com.milesight.iot.sample.annotation.entity;


import com.milesight.iot.tmp.spec.entity.annotation.DeviceEntity;
import com.milesight.iot.tmp.spec.entity.annotation.IntegrationEntities;
import com.milesight.iot.tmp.spec.entity.enums.AccessMod;
import com.milesight.iot.tmp.spec.entity.enums.Type;

/**
 * @author leon
 */
@IntegrationEntities
public class DemoGroupIntegrationEntities {

//    @DeviceEntity(key = "integration.${integration.name}.connect", accessMod = AccessMod.READ_WRITE, type = Type.SERVICE)
//    private String connect;

    @DeviceEntity(key = "integration.${integration.name}.connect.accessKey", accessMod = AccessMod.READ_WRITE, type = Type.SERVICE)
    private String connectAccessKey;

    @DeviceEntity(key = "integration.${integration.name}.connect.accessSecret", accessMod = AccessMod.READ_WRITE, type = Type.SERVICE)
    private String connectAccessSecret;
}
