package com.milesight.iab.device.model.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchDeleteDeviceRequest {
    private List<String> deviceIdList;
}
