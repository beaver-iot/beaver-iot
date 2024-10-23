package com.milesight.iab.device.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.device.po.DevicePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DeviceServiceHelper {
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    public Device convertPO(DevicePO devicePO) {
        DeviceBuilder deviceBuilder = new DeviceBuilder(integrationServiceProvider.getIntegration(devicePO.getIntegration()));
        try {
            deviceBuilder.additional(new ObjectMapper().readValue(devicePO.getAdditionalData(), HashMap.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return deviceBuilder
                .name(devicePO.getName())
                .identifier(devicePO.getIdentifier())
                .id(devicePO.getId())
                .entities(entityServiceProvider.findByTargetId(devicePO.getId().toString()))
                .build();
    }
}
