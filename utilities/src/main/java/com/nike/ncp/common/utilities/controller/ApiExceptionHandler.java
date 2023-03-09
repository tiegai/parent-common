package com.nike.ncp.common.utilities.controller;

import com.nike.ncp.common.utilities.exception.ApiException;
import com.nike.ncp.common.utilities.exception.ApiExceptionResponse;
import com.nike.ncp.common.utilities.exception.ApiExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${info.app.name:unknown}")
    private String appName;

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiException(RuntimeException ex, WebRequest request) {
        ApiException exception = (ApiException) ex;
        if (exception.isWarningMessage()) {
            log.warn("api_warning_message, code={}, errors={}", exception.getStatus().value(), exception.getErrors());
        } else {
            log.error("api_exception, code={}, errors={}", exception.getStatus().value(), exception.getErrors());
        }
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

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("internal_exception, ex={}", ex.getMessage(), ex);
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, headers, status);
    }
}
