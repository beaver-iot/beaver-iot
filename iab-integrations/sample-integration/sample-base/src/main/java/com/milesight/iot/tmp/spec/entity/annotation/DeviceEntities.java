package com.milesight.iot.tmp.spec.entity.annotation;

/**
 * @author leon
 */
public @interface DeviceEntities {
    String name();

    KeyValue[] additional() default {};

    String key();
}
