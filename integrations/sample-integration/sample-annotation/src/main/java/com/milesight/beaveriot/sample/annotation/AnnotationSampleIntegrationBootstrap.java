package com.milesight.beaveriot.sample.annotation;

import com.milesight.beaveriot.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.beaveriot.context.integration.model.DeviceBuilder;
import com.milesight.beaveriot.context.integration.model.EntityBuilder;
import com.milesight.beaveriot.context.integration.enums.AccessMod;
import com.milesight.beaveriot.context.integration.enums.EntityValueType;
import com.milesight.beaveriot.context.integration.model.Device;
import com.milesight.beaveriot.context.integration.model.Entity;
import com.milesight.beaveriot.context.integration.model.Integration;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
public class AnnotationSampleIntegrationBootstrap implements IntegrationBootstrap {

    @Override
    public void onPrepared(Integration integrationConfig) {
        Entity entityConfig = new EntityBuilder()
                .property("propParent", AccessMod.W)
                .valueType(EntityValueType.STRING)
                .children()
                .valueType(EntityValueType.STRING).property("propChildren1", AccessMod.W).end()
                .children()
                .valueType(EntityValueType.STRING).property("propChildren2", AccessMod.W).end()
                .build();
        Device device = new DeviceBuilder(integrationConfig.getId())
                .name("deviceDemo1")
                .identifier("deviceDemo1")
                .entity(entityConfig)
                .build();
        integrationConfig.addInitialDevice(device);
    }

    @Override
    public void onStarted(Integration integrationConfig) {

    }

    @Override
    public void onDestroy(Integration integrationConfig) {
    }

    @Override
    public void customizeRoute(CamelContext context) throws Exception {
        IntegrationBootstrap.super.customizeRoute(context);
    }
}
