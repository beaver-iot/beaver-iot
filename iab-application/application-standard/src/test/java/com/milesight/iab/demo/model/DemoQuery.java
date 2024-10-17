package com.milesight.iab.demo.model;

import com.milesight.iab.base.page.GenericPageRequest;
import lombok.Data;

/**
 * @author leon
 */
@Data
public class DemoQuery extends GenericPageRequest {

    private String name;
    private String age;
}
