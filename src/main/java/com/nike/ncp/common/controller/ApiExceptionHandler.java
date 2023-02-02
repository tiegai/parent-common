package com.nike.ncp.common.controller;

import com.nike.ncp.common.exception.ApiException;
import com.nike.ncp.common.exception.ApiExceptionResponse;
import com.nike.ncp.common.exception.ApiExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${info.app.name:unknown}")
    private String appName;

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiException(RuntimeException ex, WebRequest request) {
        ApiException exception = (ApiException) ex;
        log.warn("api_exception, code={}, errors={}", exception.getStatus().value(), exception.getErrors());
        ApiExceptionResponse response = ApiExceptionResponse.builder().appName(appName).errors(exception.getErrors()).build();
        return handleExceptionInternal(ex, response, new HttpHeaders(), exception.getStatus(), request);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleInternalException(RuntimeException ex, WebRequest request) {
        log.error("internal_exception, ex={}", ex.getMessage(), ex);
        ApiException exception = ApiExceptions.internalError();
        ApiExceptionResponse response = ApiExceptionResponse.builder().appName(appName).errors(exception.getErrors()).build();
        return handleExceptionInternal(ex, response, new HttpHeaders(), exception.getStatus(), request);
    }
}
