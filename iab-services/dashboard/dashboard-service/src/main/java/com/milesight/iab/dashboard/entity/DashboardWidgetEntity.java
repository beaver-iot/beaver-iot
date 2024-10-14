package com.milesight.iab.dashboard.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author loong
 * @date 2024/10/14 15:10
 */
@Data
public class DashboardWidgetEntity {

    private Long id;
    private Long dashboardId;
    private String name;
    private String data;
    private Date createdAt;
    private Date updatedAt;

}
