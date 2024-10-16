package com.milesight.iab.context.api;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrapManager;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author leon
 */
public abstract class IntegrationServiceProvider {

    @Lazy
    @Autowired
    private IntegrationBootstrapManager integrationBootstrapManager;

    public abstract void save(Integration integrationConfig);

    public abstract void batchSave(Collection<Integration> integrationConfig);

    public Integration getIntegration(String integrationId) {
        return integrationBootstrapManager.getIntegrationContext().getIntegrationConfig(integrationId);
    }

    public Collection<Integration> findAllIntegrations() {
        return integrationBootstrapManager.getIntegrationContext().getAllIntegrationConfigs().values();
    }

    public List<Integration> findActiveIntegrations() {
        return integrationBootstrapManager.getIntegrationContext().getAllIntegrationConfigs().values().stream().filter(Integration::isEnabled).toList();
    }

    public List<Integration> findIntegrations(Predicate<Integration> predicate) {
        return integrationBootstrapManager.getIntegrationContext().getAllIntegrationConfigs().values().stream().filter(predicate::test).toList();
    }

}
