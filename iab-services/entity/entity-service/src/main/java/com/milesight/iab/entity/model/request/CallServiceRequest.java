package com.milesight.iab.entity.model.request;

import lombok.Data;

import java.util.Map;

/**
 * @author loong
 * @date 2024/10/17 9:17
 */
@Data
public class CallServiceRequest {

    private Long entityId;
    private Map<String,Object> exchange;

}