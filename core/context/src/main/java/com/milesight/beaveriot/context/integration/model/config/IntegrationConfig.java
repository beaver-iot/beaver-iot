package com.milesight.beaveriot.context.integration.model.config;

import com.milesight.beaveriot.context.integration.model.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@Data
public class IntegrationConfig {

    private String name;

    private String description;

    private String iconUrl;

    private boolean enabled = true;

    private String flowExchangeDownHandler;

    private String entityIdentifierAddDevice;

    private String entityIdentifierDeleteDevice;

    private List<DeviceConfig> initialDevices = new ArrayList<>();

    private List<Entity> initialEntities = new ArrayList<>();

    public Integration toIntegration(String integrationId) {

        List<Device> devices = initialDevices.stream()
                .map(deviceConfig -> new DeviceBuilder(integrationId)
                    .name(deviceConfig.getName())
                    .identifier(deviceConfig.getIdentifier())
                    .entities(deviceConfig.getEntities())
                    .build()
                )
                .collect(Collectors.toList());

        Integration integration = new IntegrationBuilder()
                .integration()
                .id(integrationId)
                .name(name)
                .description(description)
                .iconUrl(iconUrl)
                .enabled(enabled)
                .entityIdentifierAddDevice(entityIdentifierAddDevice)
                .entityIdentifierDeleteDevice(entityIdentifierDeleteDevice)
                .flowExchangeDownHandler(flowExchangeDownHandler)
                .end()
                .initialEntities(initialEntities)
                .initialDevices(devices)
                .build();

        return integration;
    }
}