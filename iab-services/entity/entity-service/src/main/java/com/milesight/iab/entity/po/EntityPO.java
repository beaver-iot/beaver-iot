package com.milesight.iab.entity.po;

import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.entity.enums.AttachTargetType;
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
    private EntityType type;
    private AccessMod accessMod;
    private Boolean syncCall;
    private String parent;
    private AttachTargetType attachTarget;
    private String attachTargetId;
    private String valueAttribute;
    private EntityValueType valueType;
    private Long createdAt;
    private Long updatedAt;

}
