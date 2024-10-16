package com.milesight.iab.base.exception;

/**
 * @author leon
 */
public class BaseException extends RuntimeException{
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
