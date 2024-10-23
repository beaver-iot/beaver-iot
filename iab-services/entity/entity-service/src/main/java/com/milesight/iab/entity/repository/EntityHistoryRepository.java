package com.milesight.iab.entity.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.entity.model.dto.EntityHistoryUnionQuery;
import com.milesight.iab.entity.po.EntityHistoryPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/16 15:32
 */
@Repository
public interface EntityHistoryRepository extends BaseRepository<EntityHistoryPO, Long> {

    //TODO
    @Modifying
    @Query(value = "", nativeQuery = true)
    List<EntityHistoryPO> findByUnionUnique(List<EntityHistoryUnionQuery> entityHistoryUnionQueries);

}
