package com.milesight.iab.dashboard.repository;

import com.milesight.iab.dashboard.po.DashboardPO;
import com.milesight.iab.data.api.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/14 17:13
 */
@Repository
public interface DashboardRepository extends BaseRepository<DashboardPO, Long> {
}
