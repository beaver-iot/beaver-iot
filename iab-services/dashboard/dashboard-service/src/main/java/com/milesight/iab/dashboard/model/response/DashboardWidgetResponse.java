package com.milesight.iab.dashboard.model.response;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/18 9:46
 */
@Data
public class DashboardWidgetResponse {

    private Long widgetId;
    private Long dashboardId;
    private String name;
    private String data;
    private Long createdAt;

}
