package com.milesight.iab.integration.model.response;

import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class IntegrationEntityData {
    private String id;
    private String key;
    private String name;
    private EntityType type;
    private Map<String, Object> valueAttribute;
    private EntityValueType valueType;
}
