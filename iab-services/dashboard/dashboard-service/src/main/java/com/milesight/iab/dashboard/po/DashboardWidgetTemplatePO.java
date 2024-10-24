package com.milesight.iab.dashboard.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author loong
 * @date 2024/10/14 15:11
 */
@Data
@Table(name = "t_dashboard_widget_template")
@Entity
@FieldNameConstants
public class DashboardWidgetTemplatePO {

    @Id
    private Long id;
    private String name;
    private String data;
    private Long createdAt;
    private Long updatedAt;

}
