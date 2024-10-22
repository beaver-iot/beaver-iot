package com.milesight.iab.eventbus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author leon
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSubscribe {

//    /**
//     * 订阅的事件类型,默认获取方法入参的第一个参数类型
//     * @return
//     */
//    Class[] value() default {};

    String eventType() default "";

    String payloadKeyExpression() default "";

    /**
     * 线程执行器，默认采用EventBus的线程池: asyncTaskExecutor
     * @return
     */
    String executor() default "";

}
