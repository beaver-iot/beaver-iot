package com.milesight.iab.device.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.device.model.request.BatchDeleteDeviceRequest;
import com.milesight.iab.device.model.request.CreateDeviceRequest;
import com.milesight.iab.device.model.request.SearchDeviceRequest;
import com.milesight.iab.device.model.request.UpdateDeviceRequest;
import com.milesight.iab.device.model.response.DeviceDetailResponse;
import com.milesight.iab.device.model.response.DeviceResponseData;
import com.milesight.iab.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @PostMapping
    public ResponseBody<String> createDevice(@RequestBody CreateDeviceRequest createDeviceRequest) {
        deviceService.createDevice(createDeviceRequest);
        return ResponseBuilder.success();
    }

    @PostMapping("/search")
    public ResponseBody<Page<DeviceResponseData>> searchDevice(@RequestBody SearchDeviceRequest searchDeviceRequest) {
        return ResponseBuilder.success(deviceService.searchDevice(searchDeviceRequest));
    }

    @PutMapping("/{deviceId}")
    public ResponseBody<Void> updateDevice(@PathVariable("deviceId") Long deviceId, @RequestBody UpdateDeviceRequest updateDeviceRequest) {
        deviceService.updateDevice(deviceId, updateDeviceRequest);
        return ResponseBuilder.success();
    }

    @PostMapping("/batch-delete")
    public ResponseBody<Void> batchDeleteDevices(@RequestBody BatchDeleteDeviceRequest batchDeleteDeviceRequest) {
        deviceService.batchDeleteDevices(batchDeleteDeviceRequest.getDeviceIdList());
        return ResponseBuilder.success();
    }

    @GetMapping("/{deviceId}")
    public ResponseBody<DeviceDetailResponse> getDeviceDetail(@PathVariable("deviceId") Long deviceId) {
        return ResponseBuilder.success(deviceService.getDeviceDetail(deviceId));
    }
}
