package com.milesight.iab.entity.model.request;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/16 15:01
 */
@Data
public class EntityHistoryQuery {

    private Long entityId;
    private Long startTimestamp;
    private Long endTimestamp;
    private Integer pageSize;
    private Integer pageNumber;

}
