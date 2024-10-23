package com.milesight.iab.device.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceNameDTO {
    private Long id;
    private String integrationId;
    private String name;
}
