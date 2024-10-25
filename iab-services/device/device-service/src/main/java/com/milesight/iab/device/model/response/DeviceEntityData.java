package com.milesight.iab.device.model.response;

import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DeviceEntityData {
    private String id;
    private String key;
    private String name;
    private EntityType type;
    private Map<String, Object> valueAttribute;
    private EntityValueType valueType;
}
