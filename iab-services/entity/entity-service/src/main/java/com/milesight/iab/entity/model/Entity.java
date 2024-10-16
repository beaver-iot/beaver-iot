package com.milesight.iab.entity.model;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/16 14:25
 */
@Data
public class Entity {

    private Long id;
    private String key;
    private String name;
    private String type;
    private String accessMod;
    private Boolean syncCall;
    private Long parent;
    private String attachTarget;
    private String attachTargetId;
    private String valueAttribute;
    private String valueType;
    private Long createdAt;
    private Long updatedAt;

}
