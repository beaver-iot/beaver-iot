package com.milesight.iab.entity.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import com.milesight.iab.entity.po.EntityPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author loong
 * @date 2024/10/16 15:32
 */
public interface EntityRepository extends BaseJpaRepository<EntityPO, Long> {

    @Modifying
    @Query(value = "delete from entity d where d.attach_target_id = ?1", nativeQuery = true)
    void deleteByTargetId(String targetId);

}
