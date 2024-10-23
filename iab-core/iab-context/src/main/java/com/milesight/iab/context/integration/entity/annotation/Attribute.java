package com.milesight.iab.context.integration.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.milesight.iab.context.integration.builder.AttributeBuilder.POSITIVE_INT_NAN;

/**
 * @author leon
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {

    String unit() default "";
    double max() default Double.NaN;
    double min() default Double.NaN;
    int maxLength() default POSITIVE_INT_NAN;
    int minLength() default POSITIVE_INT_NAN;
    int fractionDigits() default POSITIVE_INT_NAN;
    String format() default "";
    Class<? extends Enum>[] enumClass() default {};
}
