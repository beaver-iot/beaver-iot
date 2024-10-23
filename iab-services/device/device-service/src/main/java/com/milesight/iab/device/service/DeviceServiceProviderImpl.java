package com.milesight.iab.device.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.event.DeviceEvent;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import com.milesight.iab.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceProviderImpl implements DeviceServiceProvider {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceServiceHelper deviceServiceHelper;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    @Autowired
    EventBus eventBus;

    private boolean shouldPOUpdate(DevicePO devicePO, Device device) {
        boolean additionalDataNotChanged = false;
        try {
            additionalDataNotChanged = new ObjectMapper()
                    .writeValueAsString(device.getAdditional())
                    .equals(devicePO.getAdditionalData());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return !(
                devicePO.getIdentifier().equals(device.getIdentifier())
                && devicePO.getName().equals(device.getName())
                && devicePO.getKey().equals(device.getKey())
                && additionalDataNotChanged
                );
    }

    @Override
    public void save(Device device) {
        DevicePO devicePO;
        Assert.notNull(device.getIdentifier(), "Device identifier must be provided!");
        Assert.notNull(device.getIntegration(), "Integration must be provided!");

        boolean shouldCreate = false;
        boolean shouldUpdate = false;

        // check id
        if (device.getId() != null) {
            devicePO = deviceRepository.findById(device.getId()).orElse(null);
            if (devicePO == null) {
                devicePO = new DevicePO();
                devicePO.setId(device.getId());
                shouldCreate = true;
            } else {
                shouldUpdate = shouldPOUpdate(devicePO, device);
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
            } else {
                shouldUpdate = shouldPOUpdate(devicePO, device);
            }
        }

        // set device data
        devicePO.setName(device.getName());
        devicePO.setIdentifier(device.getIdentifier());
        devicePO.setKey(device.getKey());
        try {
            devicePO.setAdditionalData(new ObjectMapper().writeValueAsString(device.getAdditional()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // create or update
        if (shouldCreate) {
            // integration would not be updated
            devicePO.setIntegration(device.getIntegration().getName());
            devicePO = deviceRepository.save(devicePO);
            eventBus.publish(DeviceEvent.of(DeviceEvent.EventType.CREATED, device));
        } else if (shouldUpdate) {
            devicePO = deviceRepository.save(devicePO);
            eventBus.publish(DeviceEvent.of(DeviceEvent.EventType.UPDATED, device));
        }

        device.setId(devicePO.getId());

        device.getEntities().forEach(entityServiceProvider::save);
    }

    @Override
    public void deleteById(Long id) {
        Device device = findById(id);
        Assert.notNull(device, "Delete failed. Cannot find device " + id.toString());

        // TODO: entityServiceProvider.deleteByDeviceId(id);

        deviceRepository.deleteById(id);

        eventBus.publish(DeviceEvent.of(DeviceEvent.EventType.DELETED, device));
    }

    @Override
    public Device findById(Long id) {
        return deviceRepository
                .findOne(f -> f
                        .eq(DevicePO.Fields.id, id)
                )
                .map(deviceServiceHelper::convertPO)
                .orElse(null);
    }

    @Override
    public Device findByIdentifier(String identifier, String integrationId) {
        return deviceRepository
                .findOne(f -> f
                        .eq(DevicePO.Fields.identifier, identifier)
                        .eq(DevicePO.Fields.integration, integrationId)
                )
                .map(deviceServiceHelper::convertPO)
                .orElse(null);
    }

    @Override
    public List<Device> findAll(String integrationId) {
        return deviceRepository
                .findAll(f -> f.eq(DevicePO.Fields.integration, integrationId))
                .stream().map(deviceServiceHelper::convertPO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countAll(String integrationId) {
        return deviceRepository.count(f -> f.eq(DevicePO.Fields.integration, integrationId));
    }
}
