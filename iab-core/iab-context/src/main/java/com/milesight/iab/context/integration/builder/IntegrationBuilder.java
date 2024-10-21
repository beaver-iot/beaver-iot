package com.milesight.iab.context.integration.builder;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * IntegrationBuilder is a builder class for Integration， eg:
 *
 * Entity entityConfig = new EntityBuilder()
 *                 .property("humidity", AccessMod.RW)
 *                 .identifier("humidity")
 *                     .children()
 *                         .property("value", AccessMod.RW)
 *                         .end()
 *                     .children()
 *                         .property("unit", AccessMod.RW)
 *                         .end()
 *                     .children()
 *                         .property("timestamp", AccessMod.RW)
 *                         .end()
 *                 .build();
 *  Device device = new DeviceBuilder("myIntegration")
 *                             .name("myDevice")
 *                             .identifier("mySN")
 *                             .entity(entityConfig)
 *                             .build();
 *
 * Integration integration = new IntegrationBuilder()
 *                 .integration()
 *                     .name("myIntegration")
 *                     .description("myIntegration description")
 *                     .end()
 *                 .initialEntity(entityConfig)
 *                 .initialEntity(entityConfig)
 *                 .initialDevice(device)
 *                 .initialDevice("myDevice1","myDevice description")
 *                     .entity(entityConfig)
 *                     .entity(entityConfig)
 *                     .end()
 *                 .initialDevice()
 *                     .name("myDevice2")
 *                     .identifier("myDevice2")
 *                     .entity(entityConfig)
 *                     .entity(entityConfig)
 *                     .end()
 *                 .build()
 *                 ;
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

        public IntegrationConfigBuilder id(String id) {
            this.integrationBuilder.integration.setId(id);
            return this;
        }

        public IntegrationConfigBuilder name(String name) {
            this.integrationBuilder.integration.setName(name);
            return this;
        }

        public IntegrationConfigBuilder description(String description) {
            this.integrationBuilder.integration.setDescription(description);
            return this;
        }

        public IntegrationConfigBuilder iconUrl(String iconUrl) {
            this.integrationBuilder.integration.setIconUrl(iconUrl);
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
