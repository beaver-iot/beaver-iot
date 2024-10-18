package com.milesight.iab.dashboard.enums;

import com.milesight.iab.base.exception.ErrorCodeSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author loong
 * @date 2024/10/16 17:43
 */
@RequiredArgsConstructor
@Getter
public enum DashboardErrorCode implements ErrorCodeSpec {

    DASHBOARD_NAME_EXIST,
    ;

    private final String errorCode;
    private final String errorMessage;
    private final String detailMessage;

    DashboardErrorCode(){
        this.errorCode = name().toLowerCase();
        this.errorMessage = null;
        this.detailMessage = null;
    }

    DashboardErrorCode(String errorMessage){
        this.errorCode = name().toLowerCase();
        this.errorMessage = errorMessage;
        this.detailMessage = null;
    }

    DashboardErrorCode(String errorMessage, String detailMessage){
        this.errorCode = name().toLowerCase();
        this.errorMessage = errorMessage;
        this.detailMessage = detailMessage;
    }

}
