package com.milesight.iab.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/16 14:28
 */
@Data
@Table(name = "t_entity_latest")
@Entity
@FieldNameConstants
public class EntityLatestPO {

    @Id
    private Long id;
    private Long entityId;
    private Integer valueInt;
    private Float valueFloat;
    private Boolean valueBoolean;
    private String valueString;
    private Byte[] valueBinary;
    private Long updatedAt;

}
