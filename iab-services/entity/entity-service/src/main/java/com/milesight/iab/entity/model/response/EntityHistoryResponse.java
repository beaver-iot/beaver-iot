package com.milesight.iab.entity.model.response;

import com.milesight.iab.context.integration.enums.EntityValueType;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/21 10:51
 */
@Data
public class EntityHistoryResponse {

    private String timestamp;
    private Object value;
    private EntityValueType valueType;

}
