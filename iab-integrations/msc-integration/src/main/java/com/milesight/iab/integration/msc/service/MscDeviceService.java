package com.milesight.iab.integration.msc.service;

import com.milesight.cloud.sdk.client.model.DeviceSaveOrUpdateRequest;
import com.milesight.cloud.sdk.client.model.ThingSpec;
import com.milesight.cloud.sdk.client.model.TslPropertyDataUpdateRequest;
import com.milesight.cloud.sdk.client.model.TslServiceCallRequest;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.integration.builder.DeviceBuilder;
import com.milesight.iab.context.integration.builder.EntityBuilder;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.integration.msc.constant.MscIntegrationConstants;
import com.milesight.iab.integration.msc.entity.MscServiceEntities;
import com.milesight.iab.integration.msc.util.MscTslUtils;
import com.milesight.msc.sdk.error.MscSdkException;
import lombok.*;
import lombok.extern.slf4j.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class MscDeviceService {

    public static final String DEVICE_ID_KEY = "deviceId";
    @Lazy
    @Autowired
    private IMscClientProvider mscClientProvider;

    @Autowired
    private DeviceServiceProvider deviceServiceProvider;

    @SneakyThrows
    @EventSubscribe(payloadKeyExpression = "msc-integration.device", eventType = ExchangeEvent.EventType.DOWN)
    public void onDeviceExchangeEvent(ExchangeEvent event) {
        val exchangePayload = event.getPayload();
        val devices = exchangePayload.getExchangeEntities()
                .values()
                .stream()
                .map(Entity::getDeviceKey)
                .distinct()
                .map(deviceServiceProvider::findByKey)
                .filter(Objects::nonNull)
                .toList();
        if (devices.size() != 1) {
            log.warn("Invalid device number: {}", devices.size());
            return;
        }
        val device = devices.get(0);

        handlePropertiesPayload(device, exchangePayload);
        handleServicePayload(device, exchangePayload);
    }

    private void handleServicePayload(Device device, ExchangePayload exchangePayload) {
        val objectMapper = mscClientProvider.getMscClient().getObjectMapper();
        val servicePayload = exchangePayload.getPayloadsByEntityType(EntityType.SERVICE);
        val deviceId = (String) device.getAdditional().get(DEVICE_ID_KEY);
        val serviceGroups = MscTslUtils.convertExchangePayloadMapToGroupedJsonNode(
                objectMapper, device.getKey(), servicePayload);
        serviceGroups.forEach((serviceId, serviceProperties) ->
                mscClientProvider.getMscClient().device().callService(deviceId, TslServiceCallRequest.builder()
                        .serviceId(serviceId)
                        .inputs(serviceProperties)
                        .build()));
    }

    @SneakyThrows
    private void handlePropertiesPayload(Device device, ExchangePayload exchangePayload) {
        val objectMapper = mscClientProvider.getMscClient().getObjectMapper();
        val propertiesPayload = exchangePayload.getPayloadsByEntityType(EntityType.PROPERTY);
        val deviceId = (String) device.getAdditional().get(DEVICE_ID_KEY);
        mscClientProvider.getMscClient().device().updateProperties(deviceId, TslPropertyDataUpdateRequest.builder()
                        .properties(MscTslUtils.convertExchangePayloadMapToGroupedJsonNode(
                                objectMapper, device.getKey(), propertiesPayload))
                        .build())
                .execute();
    }

    @SneakyThrows
    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.add-device", eventType = ExchangeEvent.EventType.DOWN)
    public void onAddDevice(Event<MscServiceEntities.AddDevice> event) {
        val deviceName = (String) event.getPayload().getContext().getOrDefault("name", "Device Name");
        if (mscClientProvider == null || mscClientProvider.getMscClient() == null) {
            log.warn("MscClient not initiated.");
            return;
        }
        val identifier = event.getPayload().getSn();
        val mscClient = mscClientProvider.getMscClient();
        val addDeviceResponse = mscClient.device().attach(DeviceSaveOrUpdateRequest.builder()
                        .name(deviceName)
                        .snDevEUI(identifier)
                        .autoProvision(false)
                        .build())
                .execute()
                .body();
        if (addDeviceResponse == null || addDeviceResponse.getData() == null
                || addDeviceResponse.getData().getDeviceId() == null) {
            log.warn("Add device failed: {} {}", deviceName, identifier);
            return;
        }

        val deviceId = addDeviceResponse.getData().getDeviceId();
        log.info("Device {} added to MSC with id {}", deviceName, deviceId);

        final String deviceIdStr = String.valueOf(deviceId);
        val thingSpec = getThingSpec(deviceIdStr);

        addLocalDevice(identifier, deviceName, deviceIdStr, thingSpec);
    }

    public Device addLocalDevice(String identifier, String deviceName, String deviceId, ThingSpec thingSpec) {
        val entities = MscTslUtils.thingSpecificationToEntities(thingSpec);
        addAdditionalEntities(entities);

        val device = new DeviceBuilder(MscIntegrationConstants.INTEGRATION_IDENTIFIER, MscIntegrationConstants.INTEGRATION_IDENTIFIER)
                .name(deviceName)
                .identifier(identifier)
                .additional(Map.of("deviceId", deviceId))
                .entities(entities)
                .build();
        deviceServiceProvider.save(device);
        return device;
    }

    public Device updateLocalDevice(String identifier, String deviceName, String deviceId, ThingSpec thingSpec) {
        val entities = MscTslUtils.thingSpecificationToEntities(thingSpec);
        addAdditionalEntities(entities);

        val device = deviceServiceProvider.findByIdentifier(identifier, MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        device.setName(deviceName);
        device.setIdentifier(identifier);
        device.setAdditional(Map.of("deviceId", deviceId));
        device.setEntities(entities);
        deviceServiceProvider.save(device);
        return device;
    }

    @Nullable
    public ThingSpec getThingSpec(String deviceId) throws IOException, MscSdkException {
        val mscClient = mscClientProvider.getMscClient();
        ThingSpec thingSpec = null;
        val response = mscClient.device()
                .getThingSpecification(deviceId)
                .execute()
                .body();
        if (response != null && response.getData() != null) {
            thingSpec = response.getData();
        }
        return thingSpec;
    }

    private static void addAdditionalEntities(List<Entity> entities) {
        entities.add(new EntityBuilder()
                .identifier(MscIntegrationConstants.InternalPropertyKey.LAST_SYNC_TIME)
                .property(MscIntegrationConstants.InternalPropertyKey.LAST_SYNC_TIME, AccessMod.R)
                .valueType(EntityValueType.INT)
                .build());
    }

    @SneakyThrows
    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.delete-device", eventType = ExchangeEvent.EventType.DOWN)
    public void onDeleteDevice(Event<MscServiceEntities.DeleteDevice> event) {
        if (mscClientProvider == null || mscClientProvider.getMscClient() == null) {
            log.warn("MscClient not initiated.");
            return;
        }
        val device = deviceServiceProvider.findByIdentifier(
                event.getPayload().getIdentifier(), MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        val additionalData = device.getAdditional();
        if (additionalData == null) {
            return;
        }
        val deviceId = additionalData.get("deviceId");
        if (deviceId == null) {
            return;
        }
        mscClientProvider.getMscClient().device().delete(deviceId.toString())
                .execute();
        deviceServiceProvider.deleteById(device.getId());
    }

}