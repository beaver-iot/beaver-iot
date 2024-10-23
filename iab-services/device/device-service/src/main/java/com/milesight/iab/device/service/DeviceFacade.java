package com.milesight.iab.device.service;

import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.dto.DeviceNameSearchRequest;
import com.milesight.iab.device.facade.IDeviceFacade;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceFacade implements IDeviceFacade {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    private DeviceNameDTO convertDevicePO(DevicePO devicePO) {
        return DeviceNameDTO.builder()
                .id(devicePO.getId())
                .name(devicePO.getName())
                .integrationConfig(integrationServiceProvider.getIntegration(devicePO.getIntegration()))
                .build();
    }

    @Override
    public List<DeviceNameDTO> fuzzySearchDeviceByName(String name) {
        return deviceRepository
                .findAll(f -> f.like(DevicePO.Fields.name, name))
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceNameDTO> getDeviceNameByIntegrations(List<String> integrationIds) {
        return deviceRepository
                .findAll(f -> f.in(DevicePO.Fields.integration, integrationIds))
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceNameDTO> getDeviceNameByIds(List<Long> deviceIds) {
        return deviceRepository.findByIdIn(deviceIds)
                .stream()
                .map(this::convertDevicePO)
                .collect(Collectors.toList());
    }
}
