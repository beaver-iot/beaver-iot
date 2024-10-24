package com.milesight.iab.entity.po;

import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.enums.EntityValueType;
import com.milesight.iab.entity.enums.AttachTargetType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/16 14:25
 */
@Data
@Table(name = "t_entity")
@Entity
@FieldNameConstants
public class EntityPO {

    @Id
    private Long id;
    private String key;
    private String name;
    @Enumerated(EnumType.STRING)
    private EntityType type;
    @Enumerated(EnumType.STRING)
    private AccessMod accessMod;
    private Boolean syncCall;
    private String parent;
    @Enumerated(EnumType.STRING)
    private AttachTargetType attachTarget;
    private String attachTargetId;
    private String valueAttribute;
    @Enumerated(EnumType.STRING)
    private EntityValueType valueType;
    private Long createdAt;
    private Long updatedAt;

}
