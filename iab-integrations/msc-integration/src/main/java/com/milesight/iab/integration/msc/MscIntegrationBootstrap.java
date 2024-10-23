package com.milesight.iab.integration.msc;

import com.milesight.iab.context.integration.bootstrap.IntegrationBootstrap;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.integration.msc.service.MscConnectionService;
import com.milesight.iab.integration.msc.service.MscDataSyncService;
import com.milesight.iab.integration.msc.service.MscWebhookService;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:integration.yaml")
public class MscIntegrationBootstrap implements IntegrationBootstrap {

    @Autowired
    private MscConnectionService mscConnectionService;

    @Autowired
    private MscDataSyncService mscDataFetchingService;

    @Autowired
    private MscWebhookService mscWebhookService;


    @Override
    public void onStarted(Integration integrationConfig) {
        mscConnectionService.init();
        mscDataFetchingService.init();
        mscWebhookService.init();
    }

    @Override
    public void onDestroy(Integration integrationConfig) {
        mscDataFetchingService.stop();
    }

    @Override
    public void customizeRoute(CamelContext context) throws Exception {
        IntegrationBootstrap.super.customizeRoute(context);
    }
}
