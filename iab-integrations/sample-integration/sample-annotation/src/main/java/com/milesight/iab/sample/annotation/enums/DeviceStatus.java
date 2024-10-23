package com.milesight.iab.sample.annotation.enums;

import com.milesight.iab.base.enums.EnumCode;

/**
 * 暂不支持枚举
 * @author leon
 */
public enum DeviceStatus implements EnumCode {
    ONLINE("a","online"), OFFLINE("a","online");

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
