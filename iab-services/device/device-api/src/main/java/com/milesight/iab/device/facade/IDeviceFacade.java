package com.milesight.iab.device.facade;

import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.dto.DeviceNameSearchRequest;
import org.springframework.data.domain.Page;

public interface IDeviceFacade {
    Page<DeviceNameDTO> searchDeviceByName(DeviceNameSearchRequest searchRequest);
}
