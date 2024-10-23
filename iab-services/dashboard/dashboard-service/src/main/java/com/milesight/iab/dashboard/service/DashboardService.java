package com.milesight.iab.dashboard.service;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.dashboard.convert.DashboardConvert;
import com.milesight.iab.dashboard.convert.DashboardWidgetConvert;
import com.milesight.iab.dashboard.enums.DashboardErrorCode;
import com.milesight.iab.dashboard.model.request.CreateDashboardRequest;
import com.milesight.iab.dashboard.model.request.CreateWidgetRequest;
import com.milesight.iab.dashboard.model.request.UpdateDashboardRequest;
import com.milesight.iab.dashboard.model.request.UpdateWidgetRequest;
import com.milesight.iab.dashboard.model.response.DashboardResponse;
import com.milesight.iab.dashboard.model.response.DashboardWidgetResponse;
import com.milesight.iab.dashboard.po.DashboardPO;
import com.milesight.iab.dashboard.po.DashboardWidgetPO;
import com.milesight.iab.dashboard.repository.DashboardRepository;
import com.milesight.iab.dashboard.repository.DashboardWidgetRepository;
import com.milesight.iab.dashboard.repository.DashboardWidgetTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author loong
 * @date 2024/10/14 14:46
 */
@Service
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private DashboardWidgetRepository dashboardWidgetRepository;
    @Autowired
    private DashboardWidgetTemplateRepository dashboardWidgetTemplateRepository;

    public void createDashboard(CreateDashboardRequest createDashboardRequest) {
        String name = createDashboardRequest.getName();
        DashboardPO dashboardPO = dashboardRepository.findUniqueOne(filterable -> filterable.eq(DashboardPO.Fields.name, name));
        if (dashboardPO != null) {
            throw ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build();
        }
        dashboardPO = new DashboardPO();
        dashboardPO.setId(SnowflakeUtil.nextId());
        dashboardPO.setName(name);
        dashboardPO.setCreatedAt(System.currentTimeMillis());
        dashboardRepository.save(dashboardPO);
    }

    public void updateDashboard(Long dashboardId, UpdateDashboardRequest updateDashboardRequest) {
        String name = updateDashboardRequest.getName();
        DashboardPO otherDashboardPO = dashboardRepository.findUniqueOne(filterable -> filterable.eq(DashboardPO.Fields.name, name));
        if (otherDashboardPO != null && !Objects.equals(otherDashboardPO.getId(), dashboardId)) {
            throw ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build();
        }
        DashboardPO dashboardPO = dashboardRepository.findById(dashboardId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("dashboard not exist").build());
        dashboardPO.setName(name);
        dashboardPO.setUpdatedAt(System.currentTimeMillis());
        dashboardRepository.save(dashboardPO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDashboard(Long dashboardId) {
        dashboardRepository.deleteById(dashboardId);
        dashboardWidgetRepository.deleteByDashboardId(dashboardId);
    }

    public List<DashboardResponse> getDashboards() {
        List<DashboardPO> dashboardPOList = dashboardRepository.findAll();
        if (dashboardPOList == null || dashboardPOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<DashboardResponse> dashboardResponseList = DashboardConvert.INSTANCE.convertResponseList(dashboardPOList);
        List<Long> dashboardIdList = dashboardResponseList.stream().map(DashboardResponse::getDashboardId).toList();
        List<DashboardWidgetResponse> dashboardWidgetResponseList = getWidgetsByDashBoards(dashboardIdList);
        Map<Long, List<DashboardWidgetResponse>> dashboardWidgetMap = dashboardWidgetResponseList.stream().filter(dashboardWidgetResponse -> dashboardWidgetResponse.getDashboardId() != null).collect(Collectors.groupingBy(DashboardWidgetResponse::getDashboardId));
        dashboardResponseList.forEach(dashboardResponse -> dashboardResponse.setWidgets(dashboardWidgetMap.get(dashboardResponse.getDashboardId())));
        return dashboardResponseList;
    }

    public void createWidget(Long dashboardId, CreateWidgetRequest createWidgetRequest) {
        dashboardRepository.findById(dashboardId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("dashboard not exist").build());

        DashboardWidgetPO dashboardWidgetPO = new DashboardWidgetPO();
        dashboardWidgetPO.setId(SnowflakeUtil.nextId());
        dashboardWidgetPO.setDashboardId(dashboardId);
        dashboardWidgetPO.setData(createWidgetRequest.getData());
        dashboardWidgetPO.setCreatedAt(System.currentTimeMillis());
        dashboardWidgetRepository.save(dashboardWidgetPO);
    }

    public void updateWidget(Long dashboardId, Long widgetId, UpdateWidgetRequest updateWidgetRequest) {
        DashboardWidgetPO dashboardWidgetPO = dashboardWidgetRepository.findById(widgetId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build());
        if (!Objects.equals(dashboardWidgetPO.getDashboardId(), dashboardId)) {
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build();
        }
        dashboardWidgetPO.setData(updateWidgetRequest.getData());
        dashboardWidgetPO.setUpdatedAt(System.currentTimeMillis());
        dashboardWidgetRepository.save(dashboardWidgetPO);
    }

    public void deleteWidget(Long dashboardId, Long widgetId) {
        DashboardWidgetPO dashboardWidgetPO = dashboardWidgetRepository.findById(widgetId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build());
        if (!Objects.equals(dashboardWidgetPO.getDashboardId(), dashboardId)) {
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build();
        }
        dashboardWidgetRepository.deleteById(widgetId);
    }

    public List<DashboardWidgetResponse> getWidgets(Long dashboardId) {
        List<DashboardWidgetPO> dashboardWidgetPOList = dashboardWidgetRepository.findAll(filter -> filter.eq(DashboardWidgetPO.Fields.dashboardId, dashboardId));
        return DashboardWidgetConvert.INSTANCE.convertResponseList(dashboardWidgetPOList);
    }

    private List<DashboardWidgetResponse> getWidgetsByDashBoards(List<Long> dashboardIds) {
        List<DashboardWidgetPO> dashboardWidgetPOList = dashboardWidgetRepository.findAll(filter -> filter.in(DashboardWidgetPO.Fields.dashboardId, dashboardIds));
        if (dashboardWidgetPOList == null || dashboardWidgetPOList.isEmpty()) {
            return new ArrayList<>();
        }
        return DashboardWidgetConvert.INSTANCE.convertResponseList(dashboardWidgetPOList);
    }

}
