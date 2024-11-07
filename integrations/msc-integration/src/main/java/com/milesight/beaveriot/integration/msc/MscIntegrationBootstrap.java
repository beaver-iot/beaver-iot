package com.milesight.beaveriot.integration.msc;

import com.milesight.beaveriot.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.beaveriot.context.integration.model.Integration;
import com.milesight.beaveriot.integration.msc.service.MscConnectionService;
import com.milesight.beaveriot.integration.msc.service.MscDataSyncService;
import com.milesight.beaveriot.integration.msc.service.MscWebhookService;
import lombok.extern.slf4j.*;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MscIntegrationBootstrap implements IntegrationBootstrap {

    @Autowired
    private MscConnectionService mscConnectionService;

    @Autowired
    private MscDataSyncService mscDataFetchingService;

    @Autowired
    private MscWebhookService mscWebhookService;


    @Override
    public void onPrepared(Integration integrationConfig) {

    }

    @Override
    public void onStarted(Integration integrationConfig) {
        log.info("MSC integration starting");
        mscConnectionService.init();
        mscDataFetchingService.init();
        mscWebhookService.init();
        log.info("MSC integration started");
    }

    @Override
    public void onDestroy(Integration integrationConfig) {
        log.info("MSC integration stopping");
        mscDataFetchingService.stop();
        log.info("MSC integration stopped");
    }

    @Override
    public void customizeRoute(CamelContext context) throws Exception {
        IntegrationBootstrap.super.customizeRoute(context);
    }
}
