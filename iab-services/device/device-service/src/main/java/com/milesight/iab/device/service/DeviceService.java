package com.milesight.iab.device.service;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ErrorCodeSpec;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.integration.model.event.DeviceEvent;
import com.milesight.iab.device.model.request.CreateDeviceRequest;
import com.milesight.iab.device.model.request.SearchDeviceRequest;
import com.milesight.iab.device.model.request.UpdateDeviceRequest;
import com.milesight.iab.device.model.response.DeviceResponseData;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import com.milesight.iab.eventbus.EventBus;
import com.milesight.iab.rule.RuleEngineExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    RuleEngineExecutor engineExecutor;

    @Autowired
    DeviceServiceHelper deviceServiceHelper;

    @Autowired
    EventBus eventBus;


    private Integration getIntegrationConfig(String integrationIdentifier) {
        Integration integrationConfig = integrationServiceProvider.getIntegration(integrationIdentifier);
        if (integrationConfig == null) {
            throw ServiceException
                    .with("INTEGRATION_NOT_FOUND", "integration" + integrationIdentifier + " not found!")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        return integrationConfig;
    }

    public void createDevice(CreateDeviceRequest createDeviceRequest) {
        String integrationIdentifier = createDeviceRequest.getIntegration();
        Integration integrationConfig = getIntegrationConfig(integrationIdentifier);

        // check ability to add device
        String addDeviceEntityId = integrationConfig.getEntityIdentifierAddDevice();
        if (addDeviceEntityId == null) {
            throw ServiceException
                    .with("INTEGRATION_NO_ADD_DEVICE", "integration" + integrationIdentifier + " cannot add device!")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        // call service for adding
        ExchangePayload payload = createDeviceRequest.getParamEntities();
        payload.putContext("deviceName", createDeviceRequest.getName());

        // Must return a device
        engineExecutor.exchangeDown(payload);
    }

    public Page<DeviceResponseData> searchDevice(SearchDeviceRequest searchDeviceRequest) {
        // convert to `DeviceResponseData`
        return deviceRepository
                .findAll(f -> f.like(DevicePO.Fields.name, searchDeviceRequest.getName()), searchDeviceRequest.toPageable())
                .map(((DevicePO device) -> {
            Integration integrationConfig = getIntegrationConfig(device.getIntegration());
            DeviceResponseData deviceResponseData = new DeviceResponseData();
            deviceResponseData.setId(device.getId());
            deviceResponseData.setKey(device.getKey());
            deviceResponseData.setName(device.getName());
            deviceResponseData.setIntegration(device.getIntegration());
            deviceResponseData.setAdditionalData(device.getAdditionalData());
            deviceResponseData.setUpdatedAt(device.getUpdatedAt());
            deviceResponseData.setCreatedAt(deviceResponseData.getCreatedAt());
            deviceResponseData.setDeletable(integrationConfig.getEntityIdentifierDeleteDevice() != null);
            deviceResponseData.setIntegrationName(integrationConfig.getName());
            return deviceResponseData;
        }));
    }

    public void updateDevice(Long deviceId, UpdateDeviceRequest updateDeviceRequest) {
        Optional<DevicePO> findResult = deviceRepository.findById(deviceId);
        if (findResult.isEmpty()) {
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).build();
        }

        DevicePO device = findResult.get();
        device.setName(updateDeviceRequest.getName());

        eventBus.publish(DeviceEvent.of(DeviceEvent.EventType.UPDATED, deviceServiceHelper.convertPO(device)));
        deviceRepository.save(device);

    }

    public void batchDeleteDevices(List<Long> deviceIdList) {
        List<DevicePO> devices = deviceRepository.findByIdIn(deviceIdList);
        Set<Long> foundIds = devices.stream().map(DevicePO::getId).collect(Collectors.toSet());

        // check whether all devices exist
        if (!new HashSet<>(deviceIdList).containsAll(foundIds)) {
            throw ServiceException
                    .with(ErrorCode.DATA_NO_FOUND)
                    .detailMessage("Some id not found!")
                    .build();
        }

        devices.stream().map((DevicePO device) -> {
            // check ability to delete device
            Integration integrationConfig = getIntegrationConfig(device.getIntegration());
            ExchangePayload payload = new ExchangePayload();
            String deleteDeviceEntityIdentifier = integrationConfig.getEntityIdentifierDeleteDevice();
            if (deleteDeviceEntityIdentifier == null) {
                throw ServiceException
                        .with(ErrorCode.METHOD_NOT_ALLOWED)
                        .detailMessage("integration " + device.getIntegration() + " cannot delete device!")
                        .build();
            }

            payload.put(deleteDeviceEntityIdentifier, "");
            payload.getContext().put("device", device);
            return payload;
        }).forEach((ExchangePayload payload) -> {
            // call service for deleting
            engineExecutor.exchangeDown(payload);
        });
    }
}