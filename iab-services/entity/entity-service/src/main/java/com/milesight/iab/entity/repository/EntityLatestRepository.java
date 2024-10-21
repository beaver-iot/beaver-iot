package com.milesight.iab.entity.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.entity.po.EntityLatestPO;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/16 15:33
 */
@Repository
public interface EntityLatestRepository extends BaseRepository<EntityLatestPO, Long> {
}
