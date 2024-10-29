package com.milesight.iab.entity.model.response;

import com.milesight.iab.context.integration.enums.EntityValueType;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/21 13:38
 */
@Data
public class EntityLatestResponse {

    private Object value;
    private String updatedAt;
    private EntityValueType valueType;

}
