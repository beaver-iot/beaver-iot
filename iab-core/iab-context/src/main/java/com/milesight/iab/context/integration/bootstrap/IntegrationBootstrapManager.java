package com.milesight.iab.context.integration.bootstrap;

import com.milesight.iab.base.exception.BootstrapException;
import com.milesight.iab.base.exception.ConfigurationException;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.constants.IntegrationConstants;
import com.milesight.iab.context.integration.entity.EntityLoader;
import com.milesight.iab.context.integration.IntegrationContext;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.support.YamlPropertySourceFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Slf4j
public class IntegrationBootstrapManager implements SmartInitializingSingleton {

    private PropertySourceFactory propertySourceFactory;
    private IntegrationContext integrationContext = new IntegrationContext();
    private ObjectProvider<EntityLoader> entityLoaders;
    private ObjectProvider<IntegrationBootstrap> integrationBootstrapList;
    private IntegrationServiceProvider integrationStorageProvider;

    public IntegrationBootstrapManager(ObjectProvider<EntityLoader> entityLoaders, ObjectProvider<IntegrationBootstrap> integrationBootstraps, IntegrationServiceProvider integrationStorageProvider) {
        this.entityLoaders = entityLoaders;
        this.integrationBootstrapList = integrationBootstraps;
        this.integrationStorageProvider = integrationStorageProvider;
        this.propertySourceFactory = new YamlPropertySourceFactory();
    }

    public void onStarted() {

        integrationBootstrapList.orderedStream().forEach(integrationBootstrap -> {

            PropertySource<?> integrationPropertySource = loadIntegrationPropertySource(integrationBootstrap);

            StandardEnvironment integrationEnvironment = createIntegrationEnvironment(integrationPropertySource);

            Integration integration = buildIntegrationConfig(integrationBootstrap.getClass(), integrationEnvironment);

            loadIntegrationEntityConfig(integration, integrationEnvironment);

            integrationBootstrap.onStarted(integration);

            integration.initializeProperties();

            if (!integration.validate()) {
                throw new BootstrapException("Failed to build integration config");
            }

            integrationContext.cacheIntegration(integrationBootstrap, integration, integrationEnvironment);

            int allDeviceEntitySize = integration.getInitialDevices().stream().mapToInt(device -> ObjectUtils.isEmpty(device.getEntities()) ? 0 : device.getEntities().size()).sum();

            log.debug("Integration {} started, Contains device size {}, device entity size {}, and integrated entity size {}", integration.getName(), integration.getInitialDevices().size(), allDeviceEntitySize, integration.getInitialEntities().size());
        });

        integrationStorageProvider.batchSave(integrationContext.getAllIntegrations().values());
    }

    public IntegrationContext getIntegrationContext() {
        return integrationContext;
    }

    private PropertySource<?> loadIntegrationPropertySource(IntegrationBootstrap integrationBootstrap) {
        try {
            String path = integrationBootstrap.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            FileUrlResource fileUrlResource = new FileUrlResource(path + IntegrationConstants.INTEGRATION_YAML);
            if (!fileUrlResource.exists()) {
                fileUrlResource = new FileUrlResource(path + IntegrationConstants.INTEGRATION_YML);
                if(!fileUrlResource.exists()){
                    throw new BootstrapException("Integration yaml not found, please check integration.yaml");
                }
            }
            return propertySourceFactory.createPropertySource(integrationBootstrap.getClass().getSimpleName(), new EncodedResource(fileUrlResource));
        } catch (IOException e) {
            throw new ConfigurationException("Failed to load integration yaml", e);
        }
    }

    public void onDestroy() {
        Map<String, Integration> allIntegrationConfigs = integrationContext.getAllIntegrations();
        integrationContext.getAllIntegrationBootstraps().entrySet().forEach(integrationBootstrapEntry -> {
            Integration integrationConfig = allIntegrationConfigs.get(integrationBootstrapEntry.getKey());
            integrationBootstrapEntry.getValue().onDestroy(integrationConfig);
        });
    }

    public void onEnabled(String integrationName) {
        Integration integrationConfig = integrationContext.getIntegration(integrationName);
        Assert.notNull(integrationConfig, "Integration config not found");
        integrationConfig.setEnabled(false);
        integrationContext.getIntegrationBootstrap(integrationName).onEnabled(integrationConfig);
    }

    public void onDisabled(String integrationName) {
        Integration integrationConfig = integrationContext.getIntegration(integrationName);
        Assert.notNull(integrationConfig, "Integration config not found");
        integrationConfig.setEnabled(true);
        integrationContext.getIntegrationBootstrap(integrationName).onDisabled(integrationConfig);
    }

    private StandardEnvironment createIntegrationEnvironment(PropertySource<?> integrationPropertySource) {
        return new StandardEnvironment() {
            @Override
            protected void customizePropertySources(MutablePropertySources propertySources) {
                propertySources.addFirst(integrationPropertySource);
            }
        };
    }

    @SneakyThrows
    private Integration buildIntegrationConfig(Class<? extends IntegrationBootstrap> clazz, StandardEnvironment environment) {
        BindResult<HashMap> integrationRoot = Binder.get(environment).bind(IntegrationConstants.INTEGRATION_PROPERTY_PREFIX, HashMap.class);
        if (!(integrationRoot.isBound() && integrationRoot.get() instanceof Map)) {
            throw new ConfigurationException("Integration information not configured, please check integration.yaml");
        }
        if (integrationRoot.get().size() != 1) {
            throw new ConfigurationException("Integration information not configured correctly, There is one and only one integration configuration, please check integration.yaml");
        }
        String integrationId = (String) integrationRoot.get().keySet().iterator().next();
        Integration integration = Binder.get(environment).bind(IntegrationConstants.INTEGRATION_PROPERTY_PREFIX + "." + integrationId, Integration.class).get();
        integration.setId(integrationId);
        integration.setIntegrationClass(clazz);
        return integration;
    }

    private void loadIntegrationEntityConfig(Integration integration, StandardEnvironment integrationEnvironment) {
        entityLoaders.stream().forEach(entityLoader -> {
            try {
                entityLoader.load(integration, integrationEnvironment);
            } catch (Exception e) {
                throw new BootstrapException("Failed to load entity config", e);
            }
        });
    }

    @Override
    public void afterSingletonsInstantiated() {
        onStarted();
    }
}

