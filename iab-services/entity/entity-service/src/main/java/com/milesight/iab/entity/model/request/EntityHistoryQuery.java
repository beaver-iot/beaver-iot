package com.milesight.iab.entity.model.request;

import com.milesight.iab.base.page.GenericPageRequest;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/16 15:01
 */
@Data
public class EntityHistoryQuery extends GenericPageRequest {

    private Long entityId;
    private Long startTimestamp;
    private Long endTimestamp;

}
