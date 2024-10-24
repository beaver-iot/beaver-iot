package com.milesight.iab.dashboard.repository;

import com.milesight.iab.dashboard.po.DashboardWidgetPO;
import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author loong
 * @date 2024/10/14 17:14
 */
public interface DashboardWidgetRepository extends BaseJpaRepository<DashboardWidgetPO, Long> {

    @Modifying
    @Query(value = "delete from dashboard_widget d where d.dashboard_id = ?1", nativeQuery = true)
    void deleteByDashboardId(Long dashboardId);
}
