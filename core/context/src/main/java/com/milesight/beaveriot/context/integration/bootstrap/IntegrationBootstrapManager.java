package com.milesight.beaveriot.context.integration.bootstrap;

import com.milesight.beaveriot.base.exception.BootstrapException;
import com.milesight.beaveriot.base.exception.ConfigurationException;
import com.milesight.beaveriot.context.integration.IntegrationContext;
import com.milesight.beaveriot.context.api.IntegrationServiceProvider;
import com.milesight.beaveriot.context.constants.IntegrationConstants;
import com.milesight.beaveriot.context.integration.entity.EntityLoader;
import com.milesight.beaveriot.context.integration.model.Integration;
import com.milesight.beaveriot.context.support.YamlPropertySourceFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Slf4j
@Order(0)
public class IntegrationBootstrapManager implements CommandLineRunner{

    private YamlPropertySourceFactory propertySourceFactory;
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
            try{
                long currentTimeMillis = System.currentTimeMillis();

                PropertySource<?> integrationPropertySource = loadIntegrationPropertySource(integrationBootstrap);

                StandardEnvironment integrationEnvironment = createIntegrationEnvironment(integrationPropertySource);

                Integration integration = buildIntegrationConfig(integrationBootstrap.getClass(), integrationEnvironment);

                loadIntegrationEntityConfig(integration, integrationEnvironment);

                integrationBootstrap.onPrepared(integration);

                integration.initializeProperties();

                integrationContext.cacheIntegration(integrationBootstrap, integration, integrationEnvironment);

                int allDeviceEntitySize = integration.getInitialDevices().stream().mapToInt(device -> ObjectUtils.isEmpty(device.getEntities()) ? 0 : device.getEntities().size()).sum();

                long cost = System.currentTimeMillis() - currentTimeMillis;

                log.debug("Integration {} started, Contains device size {}, device entity size {}, and integrated entity size {}, cost : {}", integration.getName(), integration.getInitialDevices().size(), allDeviceEntitySize, integration.getInitialEntities().size(),cost);
            }catch (Exception e) {
                log.error("Failed to load integration yaml", e);
            }
        });

        integrationStorageProvider.batchSave(integrationContext.getAllIntegrations().values());

        integrationBootstrapList.orderedStream().forEach(integrationBootstrap -> {
            Integration integration = integrationContext.getIntegration(integrationBootstrap);
            if(integration != null){
                integrationBootstrap.onStarted(integration);
            }
        });

        log.info("IntegrationBootstrapManager started, contains integrations : {}", integrationContext.getAllIntegrations().keySet());
    }

    public IntegrationContext getIntegrationContext() {
        return integrationContext;
    }

    @SneakyThrows
    private PropertySource<?> loadIntegrationPropertySource(IntegrationBootstrap integrationBootstrap) {
        try {
            String path = integrationBootstrap.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            return propertySourceFactory.createJarPropertySource(integrationBootstrap.getClass().getSimpleName(), path);
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
    public void run(String... args) throws Exception {
        onStarted();
    }
}

