package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.model.Device;

import java.util.List;

/**
 * @author leon
 */
public interface DeviceServiceProvider {
    void save(Device device);

    void deleteById(Long id);

    Device findById(Long id);

    Device findByIdentifier(String identifier, String integration);

    List<Device> findAll(String integration);
}
