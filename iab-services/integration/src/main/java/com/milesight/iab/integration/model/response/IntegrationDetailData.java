package com.milesight.iab.integration.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IntegrationDetailData extends SearchIntegrationResponseData {
    List<IntegrationEntityData> entities;
}
