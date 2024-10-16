package com.milesight.iab.dashboard.service;

import com.milesight.iab.dashboard.request.CreateDashboardRequest;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/14 14:46
 */
@Service
public class DashboardService {

    public String createDashboard(CreateDashboardRequest createDashboardRequest) {
        //TODO
        return "createDashboard";
    }

    public String updateDashboard(String dashboardId) {
        //TODO
        return "updateDashboard";
    }

    public String deleteDashboard(String dashboardId) {
        //TODO
        return "deleteDashboard";
    }

    public String getDashboards() {
        //TODO
        return "getDashboards";
    }

    public String createWidget(String dashboardId) {
        //TODO
        return "createWidget";
    }

    public String updateWidget(String dashboardId, String widgetId) {
        //TODO
        return "updateWidget";
    }

    public String deleteWidget(String dashboardId, String widgetId) {
        //TODO
        return "deleteWidget";
    }

    public String getWidgets(String dashboardId) {
        //TODO
        return "getWidgets";
    }

}
