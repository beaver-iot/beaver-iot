package com.milesight.iab.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/16 14:25
 */
@Data
@Table(name = "entity")
@Entity
@FieldNameConstants
public class EntityPO {

    @Id
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
