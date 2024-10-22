package com.milesight.iab.device.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceServiceProviderImpl implements DeviceServiceProvider {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    private Device convertPO(DevicePO devicePO) {
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
                .build();
    }

    @Override
    public void save(Device device) {
        DevicePO devicePO;
        Assert.isNull(device.getIdentifier(), "Device identifier must be provided!");
        Assert.isNull(device.getIntegration(), "Integration must be provided!");

        boolean shouldCreate = false;

        // check id
        if (device.getId() != null) {
            devicePO = deviceRepository.findById(device.getId()).orElse(null);
            if (devicePO == null) {
                devicePO = new DevicePO();
                devicePO.setId(device.getId());
                shouldCreate = true;
            }
        } else {
            devicePO = deviceRepository
                    .findOne(f -> f
                            .eq(DevicePO.Fields.identifier, device.getIdentifier())
                            .eq(DevicePO.Fields.integration, device.getIntegration().getName())
                    ).orElse(null);
            if (devicePO == null) {
                devicePO = new DevicePO();
                devicePO.setId(SnowflakeUtil.nextId());
                shouldCreate = true;
            }
        }

        // set device data
        devicePO.setName(device.getName());
        devicePO.setKey(device.getKey());
        devicePO.setIdentifier(device.getIdentifier());
        try {
            devicePO.setAdditionalData(new ObjectMapper().writeValueAsString(device.getAdditional()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // create or update
        if (shouldCreate) {
            // integration would not be updated
            devicePO.setIntegration(device.getIntegration().getName());
            // TODO: send create event
        } else {
            // TODO: send update event
        }

        deviceRepository.save(devicePO);
        device.setId(devicePO.getId());

        device.getEntities().forEach(entityServiceProvider::save);
    }

    @Override
    public void deleteById(Long id) {
        deviceRepository.deleteById(id);

        // TODO Send Deleted Event
    }

    @Override
    public Device findById(Long id) {
        return deviceRepository
                .findOne(f -> f
                        .eq(DevicePO.Fields.id, id)
                )
                .map(this::convertPO)
                .orElse(null);
    }

    @Override
    public Device findByIdentifier(String identifier, String integration) {
        return deviceRepository
                .findOne(f -> f
                        .eq(DevicePO.Fields.identifier, identifier)
                        .eq(DevicePO.Fields.integration, integration)
                )
                .map(this::convertPO)
                .orElse(null);
    }

    @Override
    public List<Device> findAll(String integration) {
        return deviceRepository
                .findAll(f -> f.eq("integration", integration))
                .stream().map(this::convertPO)
                .collect(Collectors.toList());
    }
}
