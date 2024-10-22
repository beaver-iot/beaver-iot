package com.milesight.iab.entity.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.entity.po.EntityHistoryPO;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/16 15:32
 */
@Repository
public interface EntityHistoryRepository extends BaseRepository<EntityHistoryPO,Long> {
}
