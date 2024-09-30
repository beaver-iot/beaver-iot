package com.milesight.iot.tmp.spec.entity.model;

import com.milesight.iot.tmp.spec.entity.enums.Type;
import com.milesight.iot.tmp.spec.base.api.IdentityKey;

import java.util.HashMap;

/**
 * @author leon
 */
public class ExchangePayload extends HashMap<String,Object> implements IdentityKey {

    private Type type;

    @Override
    public String getKey() {
        return null;
    }

//    private ExchangeContext context;
//
//    public class ExchangeContext extends HashMap<String,Object> {
//        private Type type;
//        private String name;
//        private String description;
//    }
}
