package com.milesight.iab.dashboard.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.dashboard.model.request.CreateDashboardRequest;
import com.milesight.iab.dashboard.model.request.UpdateDashboardRequest;
import com.milesight.iab.dashboard.model.response.DashboardResponse;
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

import java.util.List;

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
    public ResponseBody<Void> createDashboard(@RequestBody CreateDashboardRequest createDashboardRequest) {
        dashboardService.createDashboard(createDashboardRequest);
        return ResponseBuilder.success();
    }

    @PutMapping("/{dashboardId}")
    public ResponseBody<Void> updateDashboard(@PathVariable("dashboardId") Long dashboardId, @RequestBody UpdateDashboardRequest updateDashboardRequest) {
        dashboardService.updateDashboard(dashboardId, updateDashboardRequest);
        return ResponseBuilder.success();
    }

    @DeleteMapping("/{dashboardId}")
    public ResponseBody<Void> deleteDashboard(@PathVariable("dashboardId") Long dashboardId) {
        dashboardService.deleteDashboard(dashboardId);
        return ResponseBuilder.success();
    }

    @GetMapping("/dashboards")
    public ResponseBody<List<DashboardResponse>> getDashboards() {
        List<DashboardResponse> dashboardResponseList = dashboardService.getDashboards();
        return ResponseBuilder.success(dashboardResponseList);
    }

}
