package com.milesight.iab.entity.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/22 14:15
 */
@Data
public class EntityAggregateResponse {

    private Object value;
    private List<CountResult> countResult;

    @Getter
    @Builder
    public static class CountResult {
        private Object value;
        private Long count;

        public CountResult(Object value, Long count) {
            this.value = value;
            this.count = count;
        }
    }

}
