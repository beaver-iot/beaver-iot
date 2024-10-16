package com.milesight.iab.dashboard.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.dashboard.model.request.CreateDashboardRequest;
import com.milesight.iab.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author loong
 * @date 2024/10/14 14:45
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @PostMapping("")
    public ResponseBody createDashboard(CreateDashboardRequest createDashboardRequest) {
        return dashboardService.createDashboard(createDashboardRequest);
    }

    @PutMapping("/{dashboardId}")
    public ResponseBody updateDashboard(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.updateDashboard(dashboardId);
    }

    @DeleteMapping("/{dashboardId}")
    public ResponseBody deleteDashboard(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.deleteDashboard(dashboardId);
    }

    @GetMapping("/dashboards")
    public ResponseBody getDashboards() {
        return dashboardService.getDashboards();
    }

    @PostMapping("/{dashboardId}/widget")
    public ResponseBody createWidget(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.createWidget(dashboardId);
    }

    @PutMapping("/{dashboardId}/widget/{widgetId}")
    public ResponseBody updateWidget(@PathVariable("dashboardId") String dashboardId, @PathVariable("widgetId") String widgetId) {
        return dashboardService.updateWidget(dashboardId, widgetId);
    }

    @DeleteMapping("/{dashboardId}/widget/{widgetId}")
    public ResponseBody deleteWidget(@PathVariable("dashboardId") String dashboardId, @PathVariable("widgetId") String widgetId) {
        return dashboardService.deleteWidget(dashboardId, widgetId);
    }

    @GetMapping("/{dashboardId}/widgets")
    public ResponseBody getWidgets(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.getWidgets(dashboardId);
    }

}
