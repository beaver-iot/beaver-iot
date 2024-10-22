package com.milesight.iab.entity.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.entity.po.EntityPO;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/16 15:32
 */
@Repository
public interface EntityRepository extends BaseRepository<EntityPO, Long> {
}
