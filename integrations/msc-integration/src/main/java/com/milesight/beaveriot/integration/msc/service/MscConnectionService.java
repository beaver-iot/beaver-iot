package com.milesight.beaveriot.integration.msc.service;

import com.milesight.beaveriot.context.api.EntityValueServiceProvider;
import com.milesight.beaveriot.context.api.ExchangeFlowExecutor;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.context.integration.model.event.ExchangeEvent;
import com.milesight.beaveriot.eventbus.annotations.EventSubscribe;
import com.milesight.beaveriot.eventbus.api.Event;
import com.milesight.beaveriot.integration.msc.entity.MscConnectionPropertiesEntities;
import com.milesight.beaveriot.integration.msc.model.IntegrationStatus;
import com.milesight.msc.sdk.MscClient;
import com.milesight.msc.sdk.config.Credentials;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class MscConnectionService implements IMscClientProvider {

    private static final String OPENAPI_STATUS_KEY = MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.openapiStatus);

    @Autowired
    private EntityValueServiceProvider entityValueServiceProvider;

    @Autowired
    private ExchangeFlowExecutor exchangeFlowExecutor;

    @Getter
    private MscClient mscClient;

    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.openapi.*", eventType = ExchangeEvent.EventType.DOWN)
    public void onOpenapiPropertiesUpdate(Event<MscConnectionPropertiesEntities.Openapi> event) {
        if (isConfigChanged(event)) {
            val openapiSettings = event.getPayload();
            initConnection(openapiSettings);
            exchangeFlowExecutor.syncExchangeDown(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, IntegrationStatus.NOT_READY.name())));
        }
        testConnection();
    }

    private void initConnection(MscConnectionPropertiesEntities.Openapi openapiSettings) {
        mscClient = MscClient.builder()
                .endpoint(openapiSettings.getServerUrl())
                .credentials(Credentials.builder()
                        .clientId(openapiSettings.getClientId())
                        .clientSecret(openapiSettings.getClientSecret())
                        .build())
                .build();
    }

    private void testConnection() {
        try {
            mscClient.test();
            exchangeFlowExecutor.syncExchangeDown(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, IntegrationStatus.READY.name())));
        } catch (Exception e) {
            log.error("Error occurs while testing connection", e);
            exchangeFlowExecutor.syncExchangeDown(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, IntegrationStatus.ERROR.name())));
        }
    }

    private boolean isConfigChanged(Event<MscConnectionPropertiesEntities.Openapi> event) {
        // check if required fields are set
        if (event.getPayload().getServerUrl() == null) {
            return false;
        }
        if (event.getPayload().getClientId() == null) {
            return false;
        }
        if (event.getPayload().getClientSecret() == null) {
            return false;
        }
        // check if mscClient is initiated
        if (mscClient == null) {
            return true;
        }
        if (mscClient.getConfig() == null) {
            return true;
        }
        if (mscClient.getConfig().getCredentials() == null) {
            return true;
        }
        // check if endpoint, clientId or clientSecret changed
        if (!Objects.equals(mscClient.getConfig().getEndpoint(), event.getPayload().getServerUrl())) {
            return true;
        }
        if (!Objects.equals(mscClient.getConfig().getCredentials().getClientId(), event.getPayload().getClientId())) {
            return true;
        }
        return !Objects.equals(mscClient.getConfig().getCredentials().getClientSecret(), event.getPayload().getClientSecret());
    }

    public void init() {
        try {
            val settings = entityValueServiceProvider.findValuesByKey(
                    MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.openapi), MscConnectionPropertiesEntities.Openapi.class);
            if (!settings.isEmpty()) {
                initConnection(settings);
                testConnection();
            }
        } catch (Exception e) {
            log.error("Error occurs while initializing connection", e);
            exchangeFlowExecutor.syncExchangeDown(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, IntegrationStatus.NOT_READY.name())));
        }
    }

}
