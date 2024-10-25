package com.milesight.iab.dashboard.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/18 9:46
 */
@Data
public class DashboardWidgetDTO {

    @JsonIgnore
    private Long dashboardId;
    private String widgetId;
    private String data;

}
