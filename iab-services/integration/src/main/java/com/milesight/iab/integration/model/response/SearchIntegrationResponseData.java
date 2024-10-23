package com.milesight.iab.integration.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchIntegrationResponseData {
    private String id;
    private String icon;
    private String name;
    private String description;
    private String addDeviceServiceKey;
    private Long deviceCount;
    private Long entityCount;
}
