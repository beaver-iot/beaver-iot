package com.milesight.iab.dashboard.model.response;

import lombok.Data;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/18 9:45
 */
@Data
public class DashboardResponse {

    private Long dashboardId;
    private String name;
    private List<DashboardWidgetResponse> widgets;
    private Long createdAt;

}
