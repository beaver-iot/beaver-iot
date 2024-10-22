package com.milesight.iab.dashboard.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/14 15:10
 */
@Data
@Table(name = "dashboard_widget")
@Entity
@FieldNameConstants
public class DashboardWidgetPO {

    @Id
    private Long id;
    private Long dashboardId;
    private String data;
    private Long createdAt;
    private Long updatedAt;

}
