package com.milesight.iab.context.integration;

import com.milesight.iab.base.exception.ConfigurationException;
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
        if(integrationBootstrapCache.containsKey(integrationConfig.getId())){
            throw new ConfigurationException("Integration id already exists ：" + integrationConfig.getId());
        }
        integrationBootstrapCache.put(integrationConfig.getId(), integrationBootstrap);
        integrationCache.put(integrationConfig.getId(), integrationConfig);
        integrationEnvironmentCache.put(integrationConfig.getId(), integrationEnvironment);
    }

    public Integration getIntegration(String id) {
        return integrationCache.get(id);
    }

    public IntegrationBootstrap getIntegrationBootstrap(String id) {
        return integrationBootstrapCache.get(id);
    }

    public Map<String, Integration> getAllIntegrations(){
        return integrationCache;
    }

    public Map<String,IntegrationBootstrap> getAllIntegrationBootstraps(){
        return integrationBootstrapCache;
    }

    public StandardEnvironment getIntegrationEnvironment(String id) {
        return integrationEnvironmentCache.get(id);
    }

    public Map<String, StandardEnvironment> getAllIntegrationEnvironment(){
        return integrationEnvironmentCache;
    }

}
