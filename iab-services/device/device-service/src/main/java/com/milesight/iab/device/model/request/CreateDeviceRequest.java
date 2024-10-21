package com.milesight.iab.device.model.request;

import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.Data;

@Data
public class CreateDeviceRequest {
    private String name;

    private String integration;

    private ExchangePayload paramEntities;
}
