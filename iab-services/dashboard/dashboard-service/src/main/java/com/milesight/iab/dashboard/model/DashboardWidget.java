package com.milesight.iab.dashboard.model;

import lombok.Data;

import java.util.Date;

/**
 * @author loong
 * @date 2024/10/14 15:10
 */
@Data
public class DashboardWidget {

    private Long id;
    private Long dashboardId;
    private String name;
    private String data;
    private Long createdAt;
    private Long updatedAt;

}
