package com.milesight.iab.entity.model;

import lombok.Data;

import java.sql.Blob;

/**
 * @author loong
 * @date 2024/10/16 14:30
 */
@Data
public class EntityHistory {

    private Long id;
    private Long entityId;
    private Integer valueInt;
    private Float valueFloat;
    private Boolean valueBoolean;
    private String valueString;
    private Blob valueBinary;
    private Long timestamp;
    private Long createdAt;
    private String createdBy;

}
