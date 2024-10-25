package com.milesight.iab.entity.model.response;

import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/21 11:00
 */
@Data
public class EntityResponse {

    private String deviceName;
    private String integrationName;
    private String entityId;
    private AccessMod entityAccessMod;
    private String entityKey;
    private EntityType entityType;
    private String entityName;
    private String entityValueAttribute;
    private String entityValueType;

}
