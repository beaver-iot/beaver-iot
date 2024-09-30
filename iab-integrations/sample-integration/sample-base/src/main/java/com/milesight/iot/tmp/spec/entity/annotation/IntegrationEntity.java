package com.milesight.iot.tmp.spec.entity.annotation;


import com.milesight.iot.tmp.spec.entity.enums.AccessMod;
import com.milesight.iot.tmp.spec.entity.enums.Type;

/**
 * @author leon
 */
public @interface IntegrationEntity {

    String name() default "";
    String key() default "integration.${integration.name}.${fieldName}";
    AccessMod accessMod() default AccessMod.READ_WRITE;
    Type type() default Type.PROPERTY;

}
