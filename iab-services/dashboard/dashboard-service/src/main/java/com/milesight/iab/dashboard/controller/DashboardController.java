package com.milesight.iab.dashboard.controller;

import com.milesight.iab.dashboard.request.CreateDashboardRequest;
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
    public String createDashboard(CreateDashboardRequest createDashboardRequest) {
        return dashboardService.createDashboard(createDashboardRequest);
    }

    @PutMapping("/{dashboardId}")
    public String updateDashboard(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.updateDashboard(dashboardId);
    }

    @DeleteMapping("/{dashboardId}")
    public String deleteDashboard(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.deleteDashboard(dashboardId);
    }

    @GetMapping("/dashboards")
    public String getDashboards() {
        return dashboardService.getDashboards();
    }

    @PostMapping("/{dashboardId}/widget")
    public String createWidget(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.createWidget(dashboardId);
    }

    @PutMapping("/{dashboardId}/widget/{widgetId}")
    public String updateWidget(@PathVariable("dashboardId") String dashboardId, @PathVariable("widgetId") String widgetId) {
        return dashboardService.updateWidget(dashboardId, widgetId);
    }

    @DeleteMapping("/{dashboardId}/widget/{widgetId}")
    public String deleteWidget(@PathVariable("dashboardId") String dashboardId, @PathVariable("widgetId") String widgetId) {
        return dashboardService.deleteWidget(dashboardId, widgetId);
    }

    @GetMapping("/{dashboardId}/widgets")
    public String getWidgets(@PathVariable("dashboardId") String dashboardId) {
        return dashboardService.getWidgets(dashboardId);
    }

}
