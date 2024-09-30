package com.milesight.iot.tmp.core.eventbus.annotation;

/**
 * @author leon
 */
public @interface EventSubscribe {

    String eventType() default "";

    String payloadKey() default "";
}
