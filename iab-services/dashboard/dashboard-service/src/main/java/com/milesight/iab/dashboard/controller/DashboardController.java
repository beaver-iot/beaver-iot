package com.milesight.iab.dashboard.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.dashboard.model.request.CreateDashboardRequest;
import com.milesight.iab.dashboard.model.request.CreateWidgetRequest;
import com.milesight.iab.dashboard.model.request.UpdateDashboardRequest;
import com.milesight.iab.dashboard.model.request.UpdateWidgetRequest;
import com.milesight.iab.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseBody createDashboard(@RequestBody CreateDashboardRequest createDashboardRequest) {
        return dashboardService.createDashboard(createDashboardRequest);
    }

    @PutMapping("/{dashboardId}")
    public ResponseBody updateDashboard(@PathVariable("dashboardId") Long dashboardId, @RequestBody UpdateDashboardRequest updateDashboardRequest) {
        return dashboardService.updateDashboard(dashboardId, updateDashboardRequest);
    }

    @DeleteMapping("/{dashboardId}")
    public ResponseBody deleteDashboard(@PathVariable("dashboardId") Long dashboardId) {
        return dashboardService.deleteDashboard(dashboardId);
    }

    @GetMapping("/dashboards")
    public ResponseBody getDashboards() {
        return dashboardService.getDashboards();
    }

    @PostMapping("/{dashboardId}/widget")
    public ResponseBody createWidget(@PathVariable("dashboardId") Long dashboardId, @RequestBody CreateWidgetRequest createWidgetRequest) {
        return dashboardService.createWidget(dashboardId, createWidgetRequest);
    }

    @PutMapping("/{dashboardId}/widget/{widgetId}")
    public ResponseBody updateWidget(@PathVariable("dashboardId") Long dashboardId, @PathVariable("widgetId") Long widgetId, @RequestBody UpdateWidgetRequest updateWidgetRequest) {
        return dashboardService.updateWidget(dashboardId, widgetId, updateWidgetRequest);
    }

    @DeleteMapping("/{dashboardId}/widget/{widgetId}")
    public ResponseBody deleteWidget(@PathVariable("dashboardId") Long dashboardId, @PathVariable("widgetId") Long widgetId) {
        return dashboardService.deleteWidget(dashboardId, widgetId);
    }

    @GetMapping("/{dashboardId}/widgets")
    public ResponseBody getWidgets(@PathVariable("dashboardId") Long dashboardId) {
        return dashboardService.getWidgets(dashboardId);
    }

}
