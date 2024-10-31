package com.milesight.beaveriot.entity.model.request;

import com.milesight.beaveriot.base.page.GenericPageRequest;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/16 14:59
 */
@Data
public class EntityQuery extends GenericPageRequest {

    private String keyword;
    private EntityType entityType;
    private Boolean excludeChildren;

}
