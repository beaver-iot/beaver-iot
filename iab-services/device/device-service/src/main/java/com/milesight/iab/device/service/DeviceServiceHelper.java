package com.milesight.iab.device.service;

import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.enums.AttachTargetType;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.device.po.DevicePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceServiceHelper {
    @Lazy
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    public Device convertPO(DevicePO devicePO) {
        return this.convertPO(Collections.singletonList(devicePO)).get(0);
    }

    public List<Device> convertPO(List<DevicePO> devicePOList) {
        List<String> deviceIds = devicePOList.stream().map((devicePO -> devicePO.getId().toString())).collect(Collectors.toList());
        List<Entity> entities = entityServiceProvider.findByTargetIds(AttachTargetType.DEVICE, deviceIds);
        Map<String, List<Entity>> deviceEntityMap = new HashMap<>();
        entities.forEach((entity -> {
            String deviceKey = entity.getDeviceKey();
            Assert.notNull(deviceKey, "Device entity must have device key");
            deviceEntityMap
                    .computeIfAbsent(deviceKey, k -> new ArrayList<>())
                    .add(entity);
        }));
        return devicePOList.stream().map((devicePO -> new DeviceBuilder(integrationServiceProvider.getIntegration(devicePO.getIntegration()))
                .name(devicePO.getName())
                .identifier(devicePO.getIdentifier())
                .id(devicePO.getId())
                .additional(devicePO.getAdditionalData())
                .entities(deviceEntityMap.get(devicePO.getKey()))
                .build())).collect(Collectors.toList());
    }

    public boolean deviceAdditionalDataEqual(Map<String, Object> arg1, Map<String, Object> arg2) {
        if (arg1 == null && arg2 == null) {
            return true;
        }

        if (arg1 == null || arg2 == null) {
            return false;
        }

        return arg1.equals(arg2);
    }
}
