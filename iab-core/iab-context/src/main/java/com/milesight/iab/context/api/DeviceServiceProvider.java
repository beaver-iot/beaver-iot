package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.model.Device;

import java.util.List;

/**
 * @author leon
 */
public interface DeviceServiceProvider {
    void save(Device device);

    void deleteById(Long id);

    Device findById(Long id, String integration);

    Device findByExternalId(Long externalId, String integration);

    List<Device> findAll(String integration);
}
