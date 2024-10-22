package com.milesight.iab.dashboard.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/14 15:09
 */
@Data
@Table(name = "dashboard")
@Entity
@FieldNameConstants
public class DashboardPO {

    @Id
    private Long id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

}
