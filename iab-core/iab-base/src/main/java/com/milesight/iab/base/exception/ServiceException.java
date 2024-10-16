package com.milesight.iab.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * @author leon
 */
public class ServiceException extends BaseException{

    public static int DEFAULT_STATUS_INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private int status;

    protected String code;

    protected String errorMessage;

    protected String detailMessage;

    protected Object args = null;

    public ServiceException(int status, String code, String message, String detailMessage, Object args, Throwable throwable) {
        super(StringUtils.isEmpty(message) ? code : message, throwable) ;
        this.status = status;
        this.code = code;
        this.errorMessage = message;
        this.detailMessage = detailMessage;
        this.args = args;
    }

    public ServiceException( String code, String message) {
        this(DEFAULT_STATUS_INTERNAL_SERVER_ERROR, code, message, null,null,null);
    }
    public ServiceException( String code, String message, Object args) {
        this(DEFAULT_STATUS_INTERNAL_SERVER_ERROR, code, message, null, args,null);
    }
    public ServiceException( String code, String message, Throwable throwable) {
        this(DEFAULT_STATUS_INTERNAL_SERVER_ERROR, code, message, null,null,throwable);
    }
    public ServiceException( String code, String message, Object args, Throwable throwable) {
        this(DEFAULT_STATUS_INTERNAL_SERVER_ERROR, code, message, null, args,throwable);
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public Object getArgs() {
        return args;
    }

    public ServiceException detailMessage(String detailMessage){
        this.detailMessage = detailMessage;
        return this;
    }
    public ServiceException args(Object data){
        this.args = data;
        return this;
    }

    public static ServiceExceptionBuilder with(String code, String message){
        return new ServiceExceptionBuilder(code, message);
    }

    public static class ServiceExceptionBuilder{
        /**
         * Http response status code, default 500
         */
        private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        /**
         * exception code
         */
        private String code;
        /**
         * exception information
         */
        private String message;
        /**
         * Detailed exception information
         */
        private String detailMessage;
        /**
         * Parameters corresponding to the error code can be used for front-end translation
         */
        private Object args = null;

        private Throwable throwable;

        public ServiceExceptionBuilder(String code, String message){
            this.code = code;
            this.message = message;
        }
        public ServiceExceptionBuilder status(int status){
            this.status = status;
            return this;
        }
        public ServiceExceptionBuilder detailMessage(String detailMessage){
            this.detailMessage = detailMessage;
            return this;
        }
        public ServiceExceptionBuilder args(Object args){
            this.args = args;
            return this;
        }
        public ServiceExceptionBuilder throwable(Throwable throwable){
            this.throwable = throwable;
            return this;
        }
        public ServiceException build(){
            return new ServiceException(status, code, message, detailMessage, args, throwable);
        }
    }
}
