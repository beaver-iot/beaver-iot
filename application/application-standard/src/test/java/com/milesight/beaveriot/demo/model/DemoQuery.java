package com.milesight.beaveriot.demo.model;

import com.milesight.beaveriot.base.page.GenericPageRequest;
import lombok.Data;

/**
 * @author leon
 */
@Data
public class DemoQuery extends GenericPageRequest {

    private String name;
    private String key;
}
