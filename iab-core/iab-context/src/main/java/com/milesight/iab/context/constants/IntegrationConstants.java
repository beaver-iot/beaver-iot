package com.milesight.iab.context.constants;

import org.springframework.util.Assert;

import java.text.MessageFormat;

/**
 * @author leon
 */
public class IntegrationConstants {

    private IntegrationConstants() {
    }

    public static final String INTEGRATION_YAML = "integration.yaml";

    public static final String INTEGRATION_YML = "integration.yml";

    public static final String INTEGRATION_PROPERTY_PREFIX = "integration";

    public static final String INTEGRATION_PROPERTY_NAME = "name";

    /**
     * Integration key format， format: {integrationName}.device.{deviceIdentifier}
     */
    public static final String INTEGRATION_DEVICE_KEY_FORMAT = "{0}.device.{1}";

    /**
     * Integration entity key format， format: {integrationName}.device.{deviceIdentifier}.{entityIdentifier}
     */
    public static final String INTEGRATION_DEVICE_ENTITY_KEY_FORMAT = "{0}.device.{1}.{2}";

    /**
     * Integration entity key format， format: {integrationName}.integration.{entityIdentifier}
     */
    public static final String INTEGRATION_ENTITY_KEY_FORMAT = "{0}.integration.{1}";

    public static final String formatIntegrationDeviceKey(String integrationName, String deviceIdentifier) {
        Assert.notNull(integrationName, "integrationName must not be null");
        Assert.notNull(deviceIdentifier, "deviceIdentifier must not be null");
        return MessageFormat.format(INTEGRATION_DEVICE_KEY_FORMAT, integrationName, deviceIdentifier);
    }

    public static final String formatIntegrationDeviceEntityKey(String integrationName, String deviceIdentifier, String entityIdentifier) {
        Assert.notNull(integrationName, "integrationName must not be null");
        Assert.notNull(deviceIdentifier, "deviceIdentifier must not be null");
        Assert.notNull(entityIdentifier, "entityIdentifier must not be null");
        return MessageFormat.format(INTEGRATION_DEVICE_ENTITY_KEY_FORMAT, integrationName, deviceIdentifier, entityIdentifier);
    }

    public static final String formatIntegrationEntityKey(String integrationName, String entityIdentifier) {
        Assert.notNull(integrationName, "integrationName must not be null");
        Assert.notNull(entityIdentifier, "entityIdentifier must not be null");
        return MessageFormat.format(INTEGRATION_ENTITY_KEY_FORMAT, integrationName, entityIdentifier);
    }
}
