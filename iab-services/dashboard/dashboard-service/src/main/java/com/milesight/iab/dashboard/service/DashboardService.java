package com.milesight.iab.dashboard.service;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.snowflake.SnowflakeUtil;
import com.milesight.iab.dashboard.convert.DashboardConvert;
import com.milesight.iab.dashboard.convert.DashboardWidgetConvert;
import com.milesight.iab.dashboard.enums.DashboardErrorCode;
import com.milesight.iab.dashboard.model.dto.DashboardWidgetDTO;
import com.milesight.iab.dashboard.model.request.CreateDashboardRequest;
import com.milesight.iab.dashboard.model.request.UpdateDashboardRequest;
import com.milesight.iab.dashboard.model.response.DashboardResponse;
import com.milesight.iab.dashboard.po.DashboardPO;
import com.milesight.iab.dashboard.po.DashboardWidgetPO;
import com.milesight.iab.dashboard.repository.DashboardRepository;
import com.milesight.iab.dashboard.repository.DashboardWidgetRepository;
import com.milesight.iab.dashboard.repository.DashboardWidgetTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
        if (!StringUtils.hasText(name)) {
            throw ServiceException.with(ErrorCode.PARAMETER_SYNTAX_ERROR).detailMessage("name is empty").build();
        }
        DashboardPO dashboardPO = dashboardRepository.findOne(filterable -> filterable.eq(DashboardPO.Fields.name, name)).orElseThrow(() -> ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build());
        dashboardPO = new DashboardPO();
        dashboardPO.setId(SnowflakeUtil.nextId());
        dashboardPO.setName(name);
        dashboardRepository.save(dashboardPO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDashboard(Long dashboardId, UpdateDashboardRequest updateDashboardRequest) {
        String name = updateDashboardRequest.getName();
        if (!StringUtils.hasText(name)) {
            throw ServiceException.with(ErrorCode.PARAMETER_SYNTAX_ERROR).detailMessage("name is empty").build();
        }
        DashboardPO otherDashboardPO = dashboardRepository.findOne(filterable -> filterable.eq(DashboardPO.Fields.name, name)).orElseThrow(() -> ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build());
        if (!Objects.equals(otherDashboardPO.getId(), dashboardId)) {
            throw ServiceException.with(DashboardErrorCode.DASHBOARD_NAME_EXIST).build();
        }
        DashboardPO dashboardPO = dashboardRepository.findById(dashboardId).orElseThrow(() -> ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("dashboard not exist").build());
        dashboardPO.setName(name);
        dashboardRepository.save(dashboardPO);

        List<DashboardWidgetDTO> dashboardWidgetDTOList = updateDashboardRequest.getWidgets();
        if (dashboardWidgetDTOList != null && !dashboardWidgetDTOList.isEmpty()) {
            List<DashboardWidgetPO> dataDashboardWidgetPOList = dashboardWidgetRepository.findAll(filter -> filter.eq(DashboardWidgetPO.Fields.dashboardId, dashboardId));
            Map<String, DashboardWidgetPO> dashboardWidgetPOMap = new HashMap<>();
            if (dataDashboardWidgetPOList != null && !dataDashboardWidgetPOList.isEmpty()) {
                dashboardWidgetPOMap.putAll(dataDashboardWidgetPOList.stream().collect(Collectors.toMap(t -> String.valueOf(t.getId()), Function.identity())));
            }
            List<DashboardWidgetPO> dashboardWidgetPOList = new ArrayList<>();
            dashboardWidgetDTOList.forEach(dashboardWidgetDTO -> {
                String widgetId = dashboardWidgetDTO.getWidgetId();
                String data = dashboardWidgetDTO.getData();
                if (widgetId == null) {
                    DashboardWidgetPO dashboardWidgetPO = new DashboardWidgetPO();
                    dashboardWidgetPO.setId(SnowflakeUtil.nextId());
                    dashboardWidgetPO.setDashboardId(dashboardId);
                    dashboardWidgetPO.setData(data);
                    dashboardWidgetPOList.add(dashboardWidgetPO);
                } else {
                    DashboardWidgetPO existDashboardWidgetPO = dashboardWidgetPOMap.get(widgetId);
                    if (existDashboardWidgetPO != null) {
                        existDashboardWidgetPO.setData(data);
                        dashboardWidgetPOList.add(existDashboardWidgetPO);
                    }
                }
            });
            List<Long> dashboardWidgetIdList = dashboardWidgetPOList.stream().map(DashboardWidgetPO::getId).toList();
            List<Long> deleteDashboardWidgetIdList = new ArrayList<>();
            if (dataDashboardWidgetPOList != null && !dataDashboardWidgetPOList.isEmpty()) {
                deleteDashboardWidgetIdList.addAll(dataDashboardWidgetPOList.stream().map(DashboardWidgetPO::getId).filter(id -> !dashboardWidgetIdList.contains(id)).toList());
            }
            if (!deleteDashboardWidgetIdList.isEmpty()) {
                dashboardWidgetRepository.deleteAllById(deleteDashboardWidgetIdList);
            }
            if (!dashboardWidgetPOList.isEmpty()) {
                dashboardWidgetRepository.saveAll(dashboardWidgetPOList);
            }
        }
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
        List<DashboardWidgetDTO> dashboardWidgetDTOList = getWidgetsByDashBoards(dashboardIdList);
        Map<Long, List<DashboardWidgetDTO>> dashboardWidgetMap = dashboardWidgetDTOList.stream().filter(dashboardWidgetDTO -> dashboardWidgetDTO.getDashboardId() != null).collect(Collectors.groupingBy(DashboardWidgetDTO::getDashboardId));
        dashboardResponseList.forEach(dashboardResponse -> dashboardResponse.setWidgets(dashboardWidgetMap.get(dashboardResponse.getDashboardId())));
        return dashboardResponseList;
    }

    private List<DashboardWidgetDTO> getWidgetsByDashBoards(List<Long> dashboardIds) {
        List<DashboardWidgetPO> dashboardWidgetPOList = dashboardWidgetRepository.findAll(filter -> filter.in(DashboardWidgetPO.Fields.dashboardId, dashboardIds.toArray()));
        if (dashboardWidgetPOList == null || dashboardWidgetPOList.isEmpty()) {
            return new ArrayList<>();
        }
        return DashboardWidgetConvert.INSTANCE.convertResponseList(dashboardWidgetPOList);
    }

}
