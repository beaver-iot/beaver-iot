package com.milesight.iab.entity.model.response;

import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/22 13:27
 */
@Data
public class EntityMetaResponse {

    private String key;
    private String name;
    private EntityType type;
    private AccessMod accessMod;
    private String valueAttribute;
    private EntityValueType valueType;

}
