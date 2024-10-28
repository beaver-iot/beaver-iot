package com.milesight.iab.integration.msc.service;

import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.integration.msc.constant.MscIntegrationConstants;
import com.milesight.iab.integration.msc.entity.MscConnectionPropertiesEntities;
import com.milesight.iab.integration.msc.model.WebhookPayload;
import com.milesight.msc.sdk.utils.HMacUtils;
import com.milesight.msc.sdk.utils.TimeUtils;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;


@Slf4j
@Service
public class MscWebhookService {

    private static final String WEBHOOK_STATUS_KEY = MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.webhookStatus);

    @Getter
    private boolean enabled = false;

    private Mac mac;

    @Autowired
    private EntityServiceProvider entityServiceProvider;

    @Autowired
    private DeviceServiceProvider deviceServiceProvider;

    @Lazy
    @Autowired
    private IMscClientProvider mscClientProvider;

    @Autowired
    private MscDataSyncService dataSyncService;

    public void init() {
        val webhookSettingsKey = MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.webhook);
        val webhookSettings = entityServiceProvider.findExchangeByKey(webhookSettingsKey, MscConnectionPropertiesEntities.Webhook.class);
        if (webhookSettings == null) {
            log.info("Webhook settings not found");
            return;
        }
        enabled = Boolean.TRUE.equals(webhookSettings.getEnabled());
        if (webhookSettings.getSecretKey() != null && !webhookSettings.isEmpty()) {
            mac = HMacUtils.getMac(webhookSettings.getSecretKey());
        }
        if (!enabled) {
            entityServiceProvider.saveExchange(ExchangePayload.create(WEBHOOK_STATUS_KEY, MscIntegrationConstants.IntegrationStatus.NOT_READY));
        }
    }

    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.webhook", eventType = ExchangeEvent.EventType.UP)
    public void onWebhookPropertiesUpdate(Event<MscConnectionPropertiesEntities.Webhook> event) {
        this.enabled = Boolean.TRUE.equals(event.getPayload().getEnabled());
        if (event.getPayload().getSecretKey() != null && !event.getPayload().getSecretKey().isEmpty()) {
            mac = HMacUtils.getMac(event.getPayload().getSecretKey());
        } else {
            mac = null;
        }
    }

    public void handleWebhookData(String signature,
                                  String webhookUuid,
                                  String requestTimestamp,
                                  String requestNonce,
                                  WebhookPayload webhookPayload) {

        log.debug("Received webhook data: {} {} {} {} {}", signature, webhookUuid, requestTimestamp, requestNonce, webhookPayload);
        if (!enabled) {
            log.debug("Webhook is disabled.");
            return;
        }

        val currentSeconds = TimeUtils.currentTimeSeconds();
        if (Long.parseLong(requestTimestamp) + 60 < currentSeconds) {
            log.warn("Webhook request outdated: {}", requestTimestamp);
            return;
        }

        if (!isSignatureValid(signature, requestTimestamp, requestNonce)) {
            log.warn("Signature invalid: {}", signature);
            return;
        }

        val eventType = webhookPayload.getEventType();
        if (eventType == null) {
            log.warn("Event type not found: {}", webhookPayload);
            return;
        }

        if ("device_data".equalsIgnoreCase(eventType)) {
            handleDeviceData(webhookPayload);
        } else {
            log.debug("Ignored event type: {}", eventType);
        }
    }

    private void handleDeviceData(WebhookPayload webhookPayload) {
        if (webhookPayload.getData() == null) {
            log.warn("Webhook data is null: {}", webhookPayload);
            return;
        }
        val client = mscClientProvider.getMscClient();
        val deviceData = client.getObjectMapper().convertValue(webhookPayload.getData(), WebhookPayload.DeviceData.class);
        if (!"PROPERTY".equalsIgnoreCase(deviceData.getType())) {
            log.debug("Not properties event: {}", deviceData.getType());
            return;
        }
        val properties = deviceData.getPayload();
        val profile = deviceData.getDeviceProfile();
        if (properties == null || profile == null) {
            log.warn("Invalid data: {}", deviceData);
            return;
        }

        val sn = deviceData.getDeviceProfile().getSn();
        val device = deviceServiceProvider.findByIdentifier(sn, MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        if (device == null) {
            log.warn("Device not added, try to sync data: {}", sn);
            dataSyncService.syncDeviceData(new MscDataSyncService.Task(MscDataSyncService.Task.Type.ADD_LOCAL_DEVICE, sn, null));
            return;
        }

        // save data
        dataSyncService.saveHistoryData(device.getKey(), properties, webhookPayload.getEventCreatedTime() * 1000);
        entityServiceProvider.saveExchange(ExchangePayload.create(WEBHOOK_STATUS_KEY, MscIntegrationConstants.IntegrationStatus.READY));
    }

    public boolean isSignatureValid(String signature, String requestTimestamp, String requestNonce) {
        if (mac != null) {
            val expectedSignature = HMacUtils.digestHex(mac, String.format("%s%s", requestTimestamp, requestNonce));
            return expectedSignature.equals(signature);
        }
        return true;
    }

}
