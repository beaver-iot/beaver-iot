package com.milesight.iab.context.integration;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 */
public class IntegrationContext {

    private Map<String, IntegrationBootstrap> integrationBootstrapCache = new ConcurrentHashMap<>();

    private Map<String, StandardEnvironment> integrationEnvironmentCache = new ConcurrentHashMap<>();

    private Map<String, Integration> integrationCache = new ConcurrentHashMap<>();

    public void cacheIntegration(IntegrationBootstrap integrationBootstrap, Integration integrationConfig, StandardEnvironment integrationEnvironment) {
        integrationBootstrapCache.put(integrationConfig.getName(), integrationBootstrap);
        integrationCache.put(integrationConfig.getName(), integrationConfig);
        integrationEnvironmentCache.put(integrationConfig.getName(), integrationEnvironment);
    }

    public Integration getIntegration(String name) {
        return integrationCache.get(name);
    }

    public IntegrationBootstrap getIntegrationBootstrap(String name) {
        return integrationBootstrapCache.get(name);
    }

    public Map<String, Integration> getAllIntegrations(){
        return integrationCache;
    }

    public Map<String,IntegrationBootstrap> getAllIntegrationBootstraps(){
        return integrationBootstrapCache;
    }

    public StandardEnvironment getIntegrationEnvironment(String name) {
        return integrationEnvironmentCache.get(name);
    }

    public Map<String, StandardEnvironment> getAllIntegrationEnvironment(){
        return integrationEnvironmentCache;
    }

}
