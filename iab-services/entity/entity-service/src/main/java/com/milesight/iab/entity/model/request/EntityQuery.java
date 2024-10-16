package com.milesight.iab.entity.model.request;

import lombok.Data;

/**
 * @author loong
 * @date 2024/10/16 14:59
 */
@Data
public class EntityQuery {

    private String keyword;
    private Integer pageSize;
    private Integer pageNumber;

}
