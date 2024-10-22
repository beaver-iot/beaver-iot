package com.milesight.iab.entity.model.response;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/21 11:00
 */
@Data
public class EntityResponse {

    private String deviceName;
    private String integrationName;
    private String entityKey;
    private String entityName;
    private String entityValueAttribute;
    private String entityValueType;

}
