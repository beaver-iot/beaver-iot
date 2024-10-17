package com.milesight.iab.base.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.BaseException;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.response.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    @ResponseBody
    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public com.milesight.iab.base.response.ResponseBody<?> invalidFormatException(InvalidFormatException e) {
        log.error("Cause invalidFormatException: ", e);
        return ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public com.milesight.iab.base.response.ResponseBody<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        log.error("Cause MethodArgumentTypeMismatchException:", e);
        return ResponseBuilder.fail(ErrorCode.PARAMETER_SYNTAX_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public com.milesight.iab.base.response.ResponseBody<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("Cause MethodArgumentNotValidException: ", e);
        List<ObjectError> objectErrors = extractBindingErrors(e);
        return CollectionUtils.isEmpty(objectErrors) ?
                ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED, e.getMessage()):
                ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED, objectErrors);
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public com.milesight.iab.base.response.ResponseBody<?> bindExceptionHandler(BindException e) {
        log.debug("Cause BindException Detail:", e);
        List<ObjectError> objectErrors = extractBindingErrors(e);
        return CollectionUtils.isEmpty(objectErrors) ?
                ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED, e.getMessage()):
                ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED, objectErrors);
    }

    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity serviceExceptionHandler(ServiceException e) {
        log.debug("Cause ServiceException Detail:", e);
        return ResponseEntity.status(e.getStatus()).body(ResponseBuilder.fail(e));
    }

    @ResponseBody
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.milesight.iab.base.response.ResponseBody<?> baseExceptionHandler(BaseException e) {
        log.error("Cause BaseException:", e);
        return ResponseBuilder.fail(ErrorCode.SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.milesight.iab.base.response.ResponseBody<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("Cause IllegalArgumentException:", e);
        return ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.milesight.iab.base.response.ResponseBody<?> handleException(Exception e) {
        log.error("Cause Exception:", e);
        return ResponseBuilder.fail(ErrorCode.SERVER_ERROR, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.milesight.iab.base.response.ResponseBody<?> handleException(RuntimeException e) {
        log.error("Cause RuntimeException :", e);
        return ResponseBuilder.fail(ErrorCode.SERVER_ERROR, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.milesight.iab.base.response.ResponseBody<?> handleException(Throwable e) {
        log.error("Cause Throwable : ", e);
        return ResponseBuilder.fail(ErrorCode.SERVER_ERROR, e.getMessage());
    }

    // servlet exception
    /*@ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public com.milesight.iab.base.response.ResponseBody<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("Cause HttpRequestMethodNotSupportedException:", e);
        return ResponseBuilder.fail(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ResponseBody
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public com.milesight.iab.base.response.ResponseBody<?>  httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("Cause Exception:", e);
        return ResponseBuilder.fail(ErrorCode.PARAMETER_SYNTAX_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public com.milesight.iab.base.response.ResponseBody<?> validationExceptionHandler(ValidationException e) {
        log.error("Cause ValidationException: {}", e.getMessage());
        log.debug("Cause ValidationException Detail:", e);
        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        return ResponseBuilder.fail(ErrorCode.PARAMETER_VALIDATION_FAILED, message);
    }*/

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        return null;
    }

    private List<ObjectError> extractBindingErrors(Throwable error) {
        BindingResult result = extractBindingResult(error);
        if (result != null && result.hasErrors()) {
            return result.getAllErrors();
        }
        return new ArrayList<>();
    }

}
