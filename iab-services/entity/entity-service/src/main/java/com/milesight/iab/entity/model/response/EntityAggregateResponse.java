package com.milesight.iab.entity.model.response;

import com.milesight.iab.context.integration.enums.EntityValueType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author loong
 * @date 2024/10/22 14:15
 */
@Data
public class EntityAggregateResponse {

    private Object value;
    private EntityValueType valueType;
    private List<CountResult> countResult;

    @Getter
    @Builder
    public static class CountResult implements Serializable {
        private Object value;
        private EntityValueType valueType;
        private Long count;

        public CountResult(Object value, EntityValueType valueType, Long count) {
            this.value = value;
            this.valueType = valueType;
            this.count = count;
        }
    }

}
