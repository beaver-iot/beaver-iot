package com.milesight.iab.dashboard.service;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.dashboard.model.request.CreateDashboardRequest;
import org.springframework.stereotype.Service;

/**
 * @author loong
 * @date 2024/10/14 14:46
 */
@Service
public class DashboardService {

    public ResponseBody createDashboard(CreateDashboardRequest createDashboardRequest) {
        //TODO
        return null;
    }

    public ResponseBody updateDashboard(String dashboardId) {
        //TODO
        return null;
    }

    public ResponseBody deleteDashboard(String dashboardId) {
        //TODO
        return null;
    }

    public ResponseBody getDashboards() {
        //TODO
        return null;
    }

    public ResponseBody createWidget(String dashboardId) {
        //TODO
        return null;
    }

    public ResponseBody updateWidget(String dashboardId, String widgetId) {
        //TODO
        return null;
    }

    public ResponseBody deleteWidget(String dashboardId, String widgetId) {
        //TODO
        return null;
    }

    public ResponseBody getWidgets(String dashboardId) {
        //TODO
        return null;
    }

}
