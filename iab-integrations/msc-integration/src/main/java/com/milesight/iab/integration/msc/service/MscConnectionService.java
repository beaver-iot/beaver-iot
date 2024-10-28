package com.milesight.iab.integration.msc.service;

import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.integration.msc.constant.MscIntegrationConstants;
import com.milesight.iab.integration.msc.entity.MscConnectionPropertiesEntities;
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
    private EntityServiceProvider entityServiceProvider;

    @Getter
    private MscClient mscClient;

    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.openapi", eventType = ExchangeEvent.EventType.UP)
    public void onOpenapiPropertiesUpdate(Event<MscConnectionPropertiesEntities.Openapi> event) {
        if (isConfigChanged(event)) {
            val openapiSettings = event.getPayload();
            initConnection(openapiSettings);
            entityServiceProvider.saveExchange(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, MscIntegrationConstants.IntegrationStatus.NOT_READY)));
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
            entityServiceProvider.saveExchange(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, MscIntegrationConstants.IntegrationStatus.READY)));
        } catch (Exception e) {
            log.error("Error occurs while testing connection", e);
            entityServiceProvider.saveExchange(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, MscIntegrationConstants.IntegrationStatus.ERROR)));
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
            val settings = entityServiceProvider.findExchangeByKey(
                    MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.openapi), MscConnectionPropertiesEntities.Openapi.class);
            if (settings != null) {
                initConnection(settings);
                testConnection();
            }
        } catch (Exception e) {
            log.error("Error occurs while initializing connection", e);
            entityServiceProvider.saveExchange(new ExchangePayload(Map.of(OPENAPI_STATUS_KEY, MscIntegrationConstants.IntegrationStatus.NOT_READY)));
        }
    }

}
