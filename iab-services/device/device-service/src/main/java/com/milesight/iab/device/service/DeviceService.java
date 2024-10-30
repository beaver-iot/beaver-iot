package com.milesight.iab.device.service;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.ExchangeFlowExecutor;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.enums.AttachTargetType;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.integration.model.event.DeviceEvent;
import com.milesight.iab.device.model.request.CreateDeviceRequest;
import com.milesight.iab.device.model.request.SearchDeviceRequest;
import com.milesight.iab.device.model.request.UpdateDeviceRequest;
import com.milesight.iab.device.model.response.DeviceDetailResponse;
import com.milesight.iab.device.model.response.DeviceEntityData;
import com.milesight.iab.device.model.response.DeviceResponseData;
import com.milesight.iab.device.po.DevicePO;
import com.milesight.iab.device.repository.DeviceRepository;
import com.milesight.iab.eventbus.EventBus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    @Autowired
    DeviceRepository deviceRepository;

    @Lazy
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    ExchangeFlowExecutor exchangeFlowExecutor;

    @Lazy
    @Autowired
    DeviceServiceHelper deviceServiceHelper;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    @Autowired
    EventBus eventBus;


    private Integration getIntegrationConfig(String integrationIdentifier) {
        Integration integrationConfig = integrationServiceProvider.getIntegration(integrationIdentifier);
        if (integrationConfig == null) {
            throw ServiceException
                    .with(ErrorCode.DATA_NO_FOUND.getErrorCode(), "integration " + integrationIdentifier + " not found!")
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
                    .with(ErrorCode.PARAMETER_VALIDATION_FAILED.getErrorCode(), "integration " + integrationIdentifier + " cannot add device!")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        // call service for adding
        ExchangePayload payload = createDeviceRequest.getParamEntities();
        payload.putContext("device_name", createDeviceRequest.getName());

        // Must return a device
        exchangeFlowExecutor.syncExchangeDown(payload);
    }

    private DeviceResponseData convertPOToResponseData(DevicePO devicePO) {
        DeviceResponseData deviceResponseData = new DeviceResponseData();
        deviceResponseData.setId(devicePO.getId().toString());
        deviceResponseData.setKey(devicePO.getKey());
        deviceResponseData.setName(devicePO.getName());
        deviceResponseData.setIntegration(devicePO.getIntegration());
        deviceResponseData.setAdditionalData(devicePO.getAdditionalData());
        deviceResponseData.setCreatedAt(devicePO.getCreatedAt());
        deviceResponseData.setUpdatedAt(devicePO.getUpdatedAt());

        Integration integrationConfig = getIntegrationConfig(devicePO.getIntegration());
        deviceResponseData.setDeletable(integrationConfig.getEntityIdentifierDeleteDevice() != null);
        deviceResponseData.setIntegrationName(integrationConfig.getName());
        return deviceResponseData;
    }

    public Page<DeviceResponseData> searchDevice(SearchDeviceRequest searchDeviceRequest) {
        // convert to `DeviceResponseData`
        return deviceRepository
                .findAll(f -> f.like(StringUtils.hasText(searchDeviceRequest.getName()), DevicePO.Fields.name, searchDeviceRequest.getName()), searchDeviceRequest.toPageable())
                .map(this::convertPOToResponseData);
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

    public void batchDeleteDevices(List<String> deviceIdList) {
        if (deviceIdList.isEmpty()) {
            return;
        }

        List<DevicePO> devices = deviceRepository.findByIdIn(deviceIdList.stream().map(Long::valueOf).collect(Collectors.toList()));
        Set<String> foundIds = devices.stream().map(id -> id.getId().toString()).collect(Collectors.toSet());

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
            String deleteDeviceServiceKey = integrationConfig.getEntityKeyDeleteDevice();
            if (deleteDeviceServiceKey == null) {
                throw ServiceException
                        .with(ErrorCode.METHOD_NOT_ALLOWED)
                        .detailMessage("integration " + device.getIntegration() + " cannot delete device!")
                        .build();
            }

            payload.put(deleteDeviceServiceKey, "");
            payload.putContext("device", device);
            return payload;
        }).forEach((ExchangePayload payload) -> {
            // call service for deleting
            exchangeFlowExecutor.syncExchangeDown(payload);
        });
    }

    public DeviceDetailResponse getDeviceDetail(Long deviceId) {
        Optional<DevicePO> findResult = deviceRepository.findById(deviceId);
        if (findResult.isEmpty()) {
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).build();
        }

        DeviceDetailResponse deviceDetailResponse = new DeviceDetailResponse();
        BeanUtils.copyProperties(convertPOToResponseData(findResult.get()), deviceDetailResponse);

        // set entities
        List<Entity> entities = entityServiceProvider.findByTargetId(AttachTargetType.DEVICE, deviceId.toString());
        deviceDetailResponse.setEntities(entities
                .stream().flatMap((Entity pEntity) -> {
                    ArrayList<Entity> flatEntities = new ArrayList<>();
                    flatEntities.add(pEntity);

                    List<Entity> childrenEntities = pEntity.getChildren();
                    if (childrenEntities != null) {
                        flatEntities.addAll(childrenEntities);
                    }

                    return flatEntities.stream().map(entity -> DeviceEntityData
                            .builder()
                            .id(entity.getId().toString())
                            .key(entity.getKey())
                            .type(entity.getType())
                            .name(entity.getName())
                            .valueType(entity.getValueType())
                            .valueAttribute(entity.getAttributes())
                            .build());
                }).toList());
        return deviceDetailResponse;
    }
}
