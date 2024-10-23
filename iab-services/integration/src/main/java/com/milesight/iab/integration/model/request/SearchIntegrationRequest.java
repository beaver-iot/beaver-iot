package com.milesight.iab.integration.model.request;

import lombok.Data;

@Data
public class SearchIntegrationRequest {
    private Boolean deviceAddable;

    private Boolean deviceDeletable;
}
