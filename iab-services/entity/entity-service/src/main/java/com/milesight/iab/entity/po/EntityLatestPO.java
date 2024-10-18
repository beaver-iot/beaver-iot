package com.milesight.iab.entity.po;

import lombok.Data;

import java.sql.Blob;

/**
 * @author loong
 * @date 2024/10/16 14:28
 */
@Data
public class EntityLatestPO {

    private Long id;
    private Long entityId;
    private Integer valueInt;
    private Float valueFloat;
    private Boolean valueBoolean;
    private String valueString;
    private Blob valueBinary;
    private Long updatedAt;

}
