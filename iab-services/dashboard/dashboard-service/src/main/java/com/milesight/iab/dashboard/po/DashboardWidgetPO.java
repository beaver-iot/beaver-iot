package com.milesight.iab.dashboard.po;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author loong
 * @date 2024/10/14 15:10
 */
@Data
@Table(name = "t_dashboard_widget")
@Entity
@FieldNameConstants
@EntityListeners(AuditingEntityListener.class)
public class DashboardWidgetPO {

    @Id
    private Long id;
    private Long dashboardId;
    private String data;
    @CreatedDate
    private Long createdAt;
    @LastModifiedDate
    private Long updatedAt;

}
