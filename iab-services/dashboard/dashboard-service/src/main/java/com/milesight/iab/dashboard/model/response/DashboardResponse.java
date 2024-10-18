package com.milesight.iab.dashboard.model.response;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/18 9:45
 */
@Data
public class DashboardResponse {

    private Long dashboardId;
    private String name;
    private Long createdAt;

}
