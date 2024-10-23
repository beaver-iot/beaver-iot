package com.milesight.iab.device.facade;

import com.milesight.iab.device.dto.DeviceNameDTO;

import java.util.List;

public interface IDeviceFacade {
    List<DeviceNameDTO> fuzzySearchDeviceByName(String name);

    List<DeviceNameDTO> getDeviceNameByIntegrations(List<String> integrations);

    List<DeviceNameDTO> getDeviceNameByIds(List<Long> ids);
}
