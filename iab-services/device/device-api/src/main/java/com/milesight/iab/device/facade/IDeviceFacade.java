package com.milesight.iab.device.facade;

import com.milesight.iab.device.dto.DeviceNameDTO;
import com.milesight.iab.device.dto.DeviceNameSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IDeviceFacade {
    List<DeviceNameDTO> fuzzySearchDeviceByName(String name);
}
