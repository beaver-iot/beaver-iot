package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.model.Device;

/**
 * @author leon
 */
public interface DeviceServiceProvider {
    void save(Device device);
}
