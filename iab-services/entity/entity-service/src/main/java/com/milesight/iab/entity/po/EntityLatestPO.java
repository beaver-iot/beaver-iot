package com.milesight.iab.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author loong
 * @date 2024/10/16 14:28
 */
@Data
@Table(name = "t_entity_latest")
@Entity
@FieldNameConstants
@EntityListeners(AuditingEntityListener.class)
public class EntityLatestPO {

    @Id
    private Long id;
    private Long entityId;
    private Integer valueInt;
    private Float valueFloat;
    private Boolean valueBoolean;
    private String valueString;
    private Byte[] valueBinary;
    private Long timestamp;
    private Long updatedAt;

}
