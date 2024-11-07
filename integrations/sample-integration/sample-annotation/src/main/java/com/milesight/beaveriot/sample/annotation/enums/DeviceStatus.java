package com.milesight.beaveriot.sample.annotation.enums;

import com.milesight.beaveriot.base.enums.EnumCode;

/**
 * 暂不支持枚举
 * @author leon
 */
public enum DeviceStatus implements EnumCode {
    ONLINE("a","online"), OFFLINE("b","offline");

    private String code;
    private String value;
    DeviceStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return value;
    }

}
