package com.milesight.iab.dashboard.entity;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/14 15:10
 */
@Data
public class DashboardWidgetPO {

    private Long id;
    private Long dashboardId;
    private String name;
    private String data;
    private Long createdAt;
    private Long updatedAt;

}
