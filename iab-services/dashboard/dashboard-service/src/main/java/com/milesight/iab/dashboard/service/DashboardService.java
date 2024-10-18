package com.milesight.iab.dashboard.service;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
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

import java.util.List;
import java.util.Objects;

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

    public ResponseBody createDashboard(CreateDashboardRequest createDashboardRequest) {
        String name = createDashboardRequest.getName();
        DashboardPO dashboardPO = null;
        //TODO
//        DashboardPO dashboardPO = dashboardRepository.getByName(name);
        if(dashboardPO != null){
            throw ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build();
        }
        dashboardPO = new DashboardPO();
        dashboardPO.setId(SnowflakeUtil.nextId());
        dashboardPO.setName(name);
        dashboardPO.setCreatedAt(System.currentTimeMillis());
        dashboardRepository.save(dashboardPO);
        return ResponseBuilder.success();
    }

    public ResponseBody updateDashboard(Long dashboardId, UpdateDashboardRequest updateDashboardRequest) {
        String name = updateDashboardRequest.getName();
        DashboardPO otherDashboardPO = null;
        //TODO
//        DashboardPO otherDashboardPO = dashboardRepository.getByName(name);
        if(otherDashboardPO != null && !Objects.equals(otherDashboardPO.getId(), dashboardId)){
            throw ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build();
        }
        dashboardRepository.findById(dashboardId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("dashboard not exist").build());

        DashboardPO updateDashboardPO = new DashboardPO();
        updateDashboardPO.setId(dashboardId);
        updateDashboardPO.setName(name);
        updateDashboardPO.setUpdatedAt(System.currentTimeMillis());
        //TODO
//        dashboardRepository.update(updateDashboardPO);
        return ResponseBuilder.success();
    }

//    @Transactional(rollbackFor = Exception.class)
    public ResponseBody deleteDashboard(Long dashboardId) {
        dashboardRepository.deleteById(dashboardId);
        //TODO
//        dashboardWidgetRepository.deleteByDashboardId(dashboardId);
        return ResponseBuilder.success();
    }

    public ResponseBody getDashboards() {
        List<DashboardPO> dashboardPOList = dashboardRepository.findAll();

        List<DashboardResponse> dashboardResponseList = DashboardConvert.INSTANCE.convertResponseList(dashboardPOList);
        return ResponseBuilder.success(dashboardResponseList);
    }

    public ResponseBody createWidget(Long dashboardId, CreateWidgetRequest createWidgetRequest) {
        dashboardRepository.findById(dashboardId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("dashboard not exist").build());

        DashboardWidgetPO dashboardWidgetPO = new DashboardWidgetPO();
        dashboardWidgetPO.setId(SnowflakeUtil.nextId());
        dashboardWidgetPO.setDashboardId(dashboardId);
        dashboardWidgetPO.setName(createWidgetRequest.getName());
        dashboardWidgetPO.setData(createWidgetRequest.getData());
        dashboardWidgetPO.setCreatedAt(System.currentTimeMillis());
        dashboardWidgetRepository.save(dashboardWidgetPO);
        return ResponseBuilder.success();
    }

    public ResponseBody updateWidget(Long dashboardId, Long widgetId, UpdateWidgetRequest updateWidgetRequest) {
        DashboardWidgetPO dashboardWidgetPO = dashboardWidgetRepository.findById(widgetId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build());
        if(!Objects.equals(dashboardWidgetPO.getDashboardId(), dashboardId)){
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build();
        }
        DashboardWidgetPO updateDashboardWidgetPO = new DashboardWidgetPO();
        updateDashboardWidgetPO.setId(widgetId);
        updateDashboardWidgetPO.setName(updateWidgetRequest.getName());
        updateDashboardWidgetPO.setData(updateWidgetRequest.getData());
        updateDashboardWidgetPO.setUpdatedAt(System.currentTimeMillis());
        //TODO
//        dashboardWidgetRepository.update(updateDashboardWidgetPO);
        return ResponseBuilder.success();
    }

    public ResponseBody deleteWidget(Long dashboardId, Long widgetId) {
        DashboardWidgetPO dashboardWidgetPO = dashboardWidgetRepository.findById(widgetId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build());
        if(!Objects.equals(dashboardWidgetPO.getDashboardId(), dashboardId)){
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("widget not exist").build();
        }
        dashboardWidgetRepository.deleteById(widgetId);
        return ResponseBuilder.success();
    }

    public ResponseBody getWidgets(Long dashboardId) {
        List<DashboardWidgetPO> dashboardWidgetPOList = null;
        //TODO
//        List<DashboardWidgetPO> dashboardWidgetPOList = dashboardWidgetRepository.findAllByDashboardId(dashboardId);

        List<DashboardWidgetResponse> dashboardWidgetResponseList = DashboardWidgetConvert.INSTANCE.convertResponseList(dashboardWidgetPOList);
        return ResponseBuilder.success(dashboardWidgetResponseList);
    }

}
