package com.milesight.beaveriot.integration.msc.model;

import com.milesight.beaveriot.base.enums.EnumCode;
import lombok.*;


@Getter
@RequiredArgsConstructor
public enum IntegrationStatus implements EnumCode {
    READY,
    NOT_READY,
    ERROR,
    ;

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
