package com.milesight.iab.base.tracer;

import java.util.UUID;

import static com.milesight.iab.base.constants.StringConstant.DASHED;

/**
 * @author leon
 */
public interface TraceIdProvider {

    String getTraceId();

    static String traceId() {
        return UUID.randomUUID().toString().replace(DASHED, "");
    }

}
