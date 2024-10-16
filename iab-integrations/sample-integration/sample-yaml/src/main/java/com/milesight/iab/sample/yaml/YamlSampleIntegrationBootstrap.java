package com.milesight.iab.sample.yaml;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.model.Integration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
public class YamlSampleIntegrationBootstrap implements IntegrationBootstrap {
    @Override
    public void onStarted(Integration integrationConfig) {

    }

    @Override
    public void onDestroy(Integration integrationConfig) {

    }
}
