package com.milesight.iab.device.po;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Data
@Entity
@FieldNameConstants
@Table(name = "device")
public class DevicePO {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "integration")
    private String integration;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "additional_data")
    private String additionalData;

    @Column(name = "created_at")
    @CreatedDate
    private Long createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Long updatedAt;
}
