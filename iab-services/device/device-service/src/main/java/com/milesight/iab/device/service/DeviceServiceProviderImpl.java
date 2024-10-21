package com.milesight.iab.device.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceServiceProviderImpl implements DeviceServiceProvider {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    private Device convertPO(DevicePO devicePO) {
        Device device = new Device();
        try {
            device.setAdditional(new ObjectMapper().readValue(devicePO.getAdditionalData(), HashMap.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        device.setId(devicePO.getId());
        device.setName(devicePO.getName());
        device.setIdentifier(devicePO.getIdentifier());
        device.setIntegration(integrationServiceProvider.getIntegration(devicePO.getIntegration()));

        // TODO set entities

        return device;
    }

    @Override
    public void save(Device device) {
        DevicePO devicePO = new DevicePO();
        devicePO.setName(device.getName());
        devicePO.setKey(device.getKey());
        try {
            devicePO.setAdditionalData(new ObjectMapper().writeValueAsString(device.getAdditional()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        devicePO.setIntegration(device.getIntegration().getName());
        devicePO.setIdentifier(device.getIdentifier());
        Long deviceId = devicePO.getId();
        deviceRepository.save(devicePO);
        if (deviceId == null) {
            // TODO: send create event
        } else {
            // TODO: send update event
        }

        // TODO save entities
    }

    @Override
    public void deleteById(Long id) {
        deviceRepository.deleteById(id);

        // TODO Send Deleted Event
    }

    @Override
    public Device findById(Long id, String integration) {
        return deviceRepository
                .findOne(f -> f
                        .eq(DevicePO.Fields.id, id)
                        .eq(DevicePO.Fields.integration, integration)
                )
                .map(this::convertPO)
                .orElse(null);
    }

    @Override
    public Device findByIdentifier(Long identifier, String integration) {
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
