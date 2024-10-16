package com.milesight.iab.context.integration.entity.annotation;

import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.enums.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author leon
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    String name() default "${fieldName}";

    String identifier() default "${fieldName}";

    AccessMod accessMod() default AccessMod.RW;

    boolean syncCall() default true;

    EntityType type() default EntityType.PROPERTY;

    KeyValue[] attributes() default {};

}
