package com.milesight.iab.integration.service;

import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.integration.model.request.SearchIntegrationRequest;
import com.milesight.iab.integration.model.response.SearchIntegrationResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IntegrationService {
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    DeviceServiceProvider deviceServiceProvider;

    public List<SearchIntegrationResponseData> searchIntegration(SearchIntegrationRequest searchDeviceRequest) {
        return integrationServiceProvider.findActiveIntegrations()
                .stream()
                .filter(integration -> {
            if (searchDeviceRequest.getDeviceAddable() != null) {
                Boolean canAddDevice = integration.getEntityIdentifierAddDevice() != null;
                if (!searchDeviceRequest.getDeviceAddable().equals(canAddDevice)) {
                    return false;
                }
            }

            if (searchDeviceRequest.getDeviceDeletable() != null) {
                Boolean canDeleteDevice = integration.getEntityIdentifierDeleteDevice() != null;
                if (!searchDeviceRequest.getDeviceDeletable().equals(canDeleteDevice)) {
                    return false;
                }
            }

            return true;
        }).map(integration -> SearchIntegrationResponseData.builder()
                .id(integration.getId())
                .icon(integration.getIconUrl())
                .name(integration.getName())
                .description(integration.getDescription())
                .addDeviceServiceKey(integration.getEntityIdentifierAddDevice())
                .deviceCount(deviceServiceProvider.countAll(integration.getId()))
                // TODO: .entityCount()
                .build()
        ).collect(Collectors.toList());
    }
}
