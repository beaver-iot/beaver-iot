package com.milesight.iab.device.dto;

import com.milesight.iab.context.integration.model.Integration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceNameDTO {
    private Long id;
    private Integration integrationConfig;
    private String name;
}