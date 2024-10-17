package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author leon
 */
public class IntegrationBuilder {

    protected Integration integration;

    public DeviceBuilder initialDevice(String name, String identifier) {
        DeviceBuilder deviceBuilder = new DeviceBuilder(this);
        return deviceBuilder.name(name).identifier(identifier);
    }

    public DeviceBuilder initialDevice() {
        return new DeviceBuilder(this);
    }

    public DeviceBuilder initialDevice(String name, String identifier, Map<String, Object> additional) {
        DeviceBuilder deviceBuilder = new DeviceBuilder(this);
        return deviceBuilder.name(name).identifier(identifier).additional(additional);
    }

    public IntegrationBuilder initialDevice(Device device) {
        Assert.notNull(integration, "integration can't be null, please set integration first");
        integration.addInitialDevice(device);
        return this;
    }


    public IntegrationBuilder initialEntity(Entity entity) {
        Assert.notNull(integration, "integration can't be null, please set integration first");
        integration.addInitialEntity(entity);
        return this;
    }

    public IntegrationConfigBuilder integration() {
        this.integration = new Integration();
        return new IntegrationConfigBuilder(this);
    }

    public IntegrationBuilder integration(String name, String description) {
        Assert.isNull(integration, "Integration can't repeat Settings");
        this.integration = Integration.of(name, description);
        return this;
    }

    public IntegrationBuilder integration(Integration integration) {
        Assert.isNull(integration, "Integration can't repeat Settings");
        this.integration = integration;
        return this;
    }

    public Integration build() {
        integration.initializeProperties();
        return integration;
    }

    public static class IntegrationConfigBuilder {

        private IntegrationBuilder integrationBuilder;

        public IntegrationConfigBuilder(IntegrationBuilder integrationBuilder) {
            this.integrationBuilder = integrationBuilder;
        }

        public IntegrationConfigBuilder name(String name) {
            this.integrationBuilder.integration.setName(name);
            return this;
        }

        public IntegrationConfigBuilder description(String description) {
            this.integrationBuilder.integration.setDescription(description);
            return this;
        }

        public IntegrationConfigBuilder integrationClass(Class<? extends IntegrationBootstrap> integrationClass) {
            this.integrationBuilder.integration.setIntegrationClass(integrationClass);
            return this;
        }

        public IntegrationConfigBuilder entityIdentifierAddDevice(String addDeviceEntityIdentifier) {
            this.integrationBuilder.integration.setEntityIdentifierAddDevice(addDeviceEntityIdentifier);
            return this;
        }

        public IntegrationConfigBuilder entityIdentifierDeleteDevice(String deleteDeviceEntityIdentifier) {
            this.integrationBuilder.integration.setEntityIdentifierDeleteDevice(deleteDeviceEntityIdentifier);
            return this;
        }

        public IntegrationBuilder end() {
            return integrationBuilder;
        }
    }
}
