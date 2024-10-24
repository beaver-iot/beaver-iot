package com.milesight.iab.dashboard.repository;

import com.milesight.iab.dashboard.po.DashboardWidgetTemplatePO;
import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/17 16:39
 */
public interface DashboardWidgetTemplateRepository extends BaseJpaRepository<DashboardWidgetTemplatePO, Long> {
}
