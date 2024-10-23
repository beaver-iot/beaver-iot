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

    Device findByKey(String deviceKey);

    Device findByIdentifier(String identifier, String integrationId);

    List<Device> findAll(String integrationId);

    Long countAll(String integrationId);
}
