package com.milesight.iab.entity.repository;

import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import com.milesight.iab.entity.model.dto.EntityHistoryUnionQuery;
import com.milesight.iab.entity.po.EntityHistoryPO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author loong
 * @date 2024/10/16 15:32
 */
public interface EntityHistoryRepository extends BaseJpaRepository<EntityHistoryPO, Long> {

    default List<EntityHistoryPO> findByUnionUnique(EntityManager entityManager, List<EntityHistoryUnionQuery> queries) {
        String dynamicQuery = generateDynamicQuery(queries);
        Query query = entityManager.createNativeQuery(dynamicQuery, EntityHistoryPO.class);

        Map<String, Object> parameters = generateParameters(queries);
        parameters.forEach(query::setParameter);

        return query.getResultList();
    }

    default String generateDynamicQuery(List<EntityHistoryUnionQuery> queries) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM entity_history WHERE ");
        for (int i = 0; i < queries.size(); i++) {
            if (i > 0) {
                sqlBuilder.append(" OR ");
            }
            sqlBuilder.append("(entity_id = :entityId").append(i).append(" AND timestamp = :timestamp").append(i).append(")");
        }
        return sqlBuilder.toString();
    }

    default Map<String, Object> generateParameters(List<EntityHistoryUnionQuery> queries) {
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < queries.size(); i++) {
            parameters.put("entityId" + i, queries.get(i).getEntityId());
            parameters.put("timestamp" + i, queries.get(i).getTimestamp());
        }
        return parameters;
    }

}
