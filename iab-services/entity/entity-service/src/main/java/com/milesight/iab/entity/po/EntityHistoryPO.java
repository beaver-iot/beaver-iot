package com.milesight.iab.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

/**
 * @author loong
 * @date 2024/10/16 14:30
 */
@Data
@Table(name = "t_entity_history")
@Entity
@FieldNameConstants
@EntityListeners(AuditingEntityListener.class)
public class EntityHistoryPO {

    @Id
    private Long id;
    private Long entityId;
    private Long valueLong;
    private BigDecimal valueDouble;
    private Boolean valueBoolean;
    private String valueString;
    private byte[] valueBinary;
    private Long timestamp;
    @CreatedDate
    private Long createdAt;
    private String createdBy;
    @LastModifiedDate
    private Long updatedAt;
    private String updatedBy;

}
