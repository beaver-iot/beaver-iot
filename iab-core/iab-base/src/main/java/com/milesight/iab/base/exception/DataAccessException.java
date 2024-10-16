package com.milesight.iab.base.exception;

/**
 * @author leon
 */
public class DataAccessException extends BaseException{
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
