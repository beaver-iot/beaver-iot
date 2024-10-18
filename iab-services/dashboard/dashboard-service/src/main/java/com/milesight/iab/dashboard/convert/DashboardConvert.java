package com.milesight.iab.dashboard.convert;

import com.milesight.iab.dashboard.model.response.DashboardResponse;
import com.milesight.iab.dashboard.po.DashboardPO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/18 9:50
 */
@Mapper
public interface DashboardConvert {

    DashboardConvert INSTANCE = Mappers.getMapper(DashboardConvert.class);

    @Mapping(source = "id", target = "dashboardId")
    DashboardResponse convertResponse(DashboardPO dashboardPO);

    @IterableMapping(qualifiedByName = "convertResponse")
    List<DashboardResponse> convertResponseList(List<DashboardPO> dashboardPOList);

}
