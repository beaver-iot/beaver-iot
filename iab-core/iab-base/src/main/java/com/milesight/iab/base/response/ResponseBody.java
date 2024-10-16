package com.milesight.iab.base.response;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T>
 */
@Getter
@Slf4j
public class ResponseBody<T>  {

    /**
     * default ResponseBody successful response status
     */
    String DEFAULT_RESPONSE_STATUS_SUCCESS = "Success";

    /**
     * Default ResponseBody failure response status
     */
    String DEFAULT_RESPONSE_STATUS_FAILED = "Failed";

    /**
     * Response Data
     */
    private T data;

    /**
     * response status
     */
    private String status;

    /**
     * request id
     */
    private String requestId;

    /**
     * Error code (returned only when the response status is Failed)
     */
    private String errorCode;

    /**
     * Error message (returned only when the response status is Failed)
     */
    private String errorMsg;

    /**
     * Detailed error description information
     */
    private String detailMsg;

    public static ResponseBody instance(){
        return new ResponseBody();
    }

    public ResponseBody onSuccess() {
        this.status = DEFAULT_RESPONSE_STATUS_SUCCESS;
        return this;
    }

    public ResponseBody onFailed() {
        this.status = DEFAULT_RESPONSE_STATUS_FAILED;
        return this;
    }

    public ResponseBody requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public ResponseBody errorCode(String errCode) {
        this.errorCode = errCode;
        return this;
    }

    public ResponseBody errorMsg(String errMsg) {
        this.errorCode = errMsg;
        return this;
    }

    public ResponseBody detailMsg(String detailMsg) {
        this.detailMsg = detailMsg;
        return this;
    }

}