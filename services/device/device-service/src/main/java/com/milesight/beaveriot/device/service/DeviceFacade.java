package com.milesight.beaveriot.device.service;

import com.milesight.beaveriot.context.api.IntegrationServiceProvider;
import com.milesight.beaveriot.device.dto.DeviceNameDTO;
import com.milesight.beaveriot.device.facade.IDeviceFacade;
import com.milesight.beaveriot.device.po.DevicePO;
import com.milesight.beaveriot.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceFacade implements IDeviceFacade {
    @Autowired
    DeviceRepository deviceRepository;

    @Lazy
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    private DeviceNameDTO convertDevicePO(DevicePO devicePO) {
        return DeviceNameDTO.builder()
                .id(devicePO.getId())
                .name(devicePO.getName())
                .key(devicePO.getKey())
                .integrationConfig(integrationServiceProvider.getIntegration(devicePO.getIntegration()))
                .build();
    }

    @Override
    public List<DeviceNameDTO> fuzzySearchDeviceByName(String name) {
        return deviceRepository
                .findAll(f -> f.likeIgnoreCase(DevicePO.Fields.name, name))
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceNameDTO> getDeviceNameByIntegrations(List<String> integrationIds) {
        if (integrationIds == null || integrationIds.isEmpty()) {
            return new ArrayList<>();
        }
        return deviceRepository
                .findAll(f -> f.in(DevicePO.Fields.integration, integrationIds.toArray()))
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceNameDTO> getDeviceNameByIds(List<Long> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return new ArrayList<>();
        }
        return deviceRepository.findByIdIn(deviceIds)
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceNameDTO> getDeviceNameByKey(List<String> deviceKeys) {
        if (deviceKeys == null || deviceKeys.isEmpty()) {
            return new ArrayList<>();
        }
        return deviceRepository.findAll(f -> f.in(DevicePO.Fields.key, deviceKeys.toArray()))
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceNameDTO getDeviceNameByKey(String deviceKey) {
        return deviceRepository.findOne(f -> f.eq(DevicePO.Fields.key, deviceKey))
                .map(this::convertDevicePO)
                .orElse(null);
    }
}