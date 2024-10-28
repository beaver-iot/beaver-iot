package com.milesight.iab.sample.annotation;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.builder.EntityBuilder;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import org.apache.camel.CamelContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
public class AnnotationSampleIntegrationBootstrap implements IntegrationBootstrap {

    @Override
    public void onStarted(Integration integrationConfig) {

        Entity entityConfig = new EntityBuilder()
                .property("propParent", AccessMod.W)
                .valueType(EntityValueType.STRING)
                .children()
                    .valueType(EntityValueType.STRING).property("propChildren1", AccessMod.W).end()
                .children()
                    .valueType(EntityValueType.STRING).property("propChildren2", AccessMod.W).end()
                .build();
        Device device = new DeviceBuilder()
                .name("deviceDemo1")
                .identifier("deviceDemo1")
                .entity(entityConfig)
                .build();
        integrationConfig.addInitialDevice(device);
    }

    @Override
    public void onDestroy(Integration integrationConfig) {
    }

    @Override
    public void customizeRoute(CamelContext context) throws Exception {
        IntegrationBootstrap.super.customizeRoute(context);
    }
}
