package com.milesight.beaveriot.base.tracer;

import com.milesight.beaveriot.base.constants.StringConstant;

import java.util.UUID;

/**
 * todo
 * @author leon
 */
public interface TraceIdProvider {

    String getTraceId();

    static String traceId() {
        return UUID.randomUUID().toString().replace(StringConstant.DASHED, "");
    }

}
