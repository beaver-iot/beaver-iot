package com.milesight.iab.integration.service;

import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class IntegrationServiceProviderImpl extends IntegrationServiceProvider {
    @Autowired
    DeviceServiceProvider deviceServiceProvider;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    @Override
    public void save(Integration integrationConfig) {
        integrationConfig.getInitialDevices().forEach(deviceServiceProvider::save);
        integrationConfig.getInitialEntities().forEach(entityServiceProvider::save);
    }

    @Override
    public void batchSave(Collection<Integration> integrationConfig) {
        integrationConfig.forEach(this::save);
    }
}
