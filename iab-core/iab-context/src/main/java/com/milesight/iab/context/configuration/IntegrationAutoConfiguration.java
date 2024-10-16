package com.milesight.iab.context.configuration;

import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.entity.EntityLoader;
import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrapManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leon
 */
@Configuration
public class IntegrationAutoConfiguration {

    @Bean(initMethod = "onStarted", destroyMethod = "onDestroy")
    @ConditionalOnMissingBean
    public IntegrationBootstrapManager integrationBootstrapManager(ObjectProvider<EntityLoader> entityLoaders, ObjectProvider<IntegrationBootstrap> integrationBootstraps, IntegrationServiceProvider integrationStorageProvider){
        return new IntegrationBootstrapManager(entityLoaders, integrationBootstraps, integrationStorageProvider);
    }

}
