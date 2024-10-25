package com.milesight.iab.device.po;

import com.milesight.iab.data.support.MapJsonConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Map;


@Data
@Entity
@FieldNameConstants
@Table(name = "t_device")
@EntityListeners(AuditingEntityListener.class)
public class DevicePO {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "\"key\"")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "integration")
    private String integration;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "additional_data")
    @Convert(converter = MapJsonConverter.class)
    private Map<String, Object> additionalData;

    @Column(name = "created_at")
    @CreatedDate
    private Long createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Long updatedAt;
}
