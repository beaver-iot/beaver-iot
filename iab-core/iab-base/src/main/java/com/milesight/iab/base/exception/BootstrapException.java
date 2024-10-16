package com.milesight.iab.base.exception;

/**
 * @author leon
 */
public class BootstrapException extends BaseException{
    public BootstrapException(String message) {
        super(message);
    }

    public BootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}
