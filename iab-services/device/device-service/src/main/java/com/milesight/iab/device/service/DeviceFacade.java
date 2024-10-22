package com.milesight.iab.device.service;

import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.dto.DeviceNameSearchRequest;
import com.milesight.iab.device.facade.IDeviceFacade;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class DeviceFacade implements IDeviceFacade {
    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public Page<DeviceNameDTO> searchDeviceByName(DeviceNameSearchRequest searchRequest) {
        return deviceRepository
                .findAll(f -> f.like(DevicePO.Fields.name, searchRequest.getName()), searchRequest.toPageable())
                .map(devicePO -> DeviceNameDTO.builder().id(devicePO.getId()).name(devicePO.getName()).build());
    }
}
