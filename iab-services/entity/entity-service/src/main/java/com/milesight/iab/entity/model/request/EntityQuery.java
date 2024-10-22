package com.milesight.iab.entity.model.request;

import com.milesight.iab.base.page.GenericPageRequest;
import lombok.Data;

/**
 * @author loong
 * @date 2024/10/16 14:59
 */
@Data
public class EntityQuery extends GenericPageRequest {

    private String keyword;

}
