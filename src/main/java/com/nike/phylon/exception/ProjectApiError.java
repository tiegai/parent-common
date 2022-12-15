package com.nike.phylon.exception;

import com.nike.backstopper.apierror.ApiError;
import com.nike.backstopper.apierror.ApiErrorBase;
import com.nike.backstopper.apierror.ApiErrorWithMetadata;
import com.nike.backstopper.apierror.projectspecificinfo.NikeCommerceProjectApiErrorsBase;
import com.nike.backstopper.apierror.projectspecificinfo.ProjectSpecificErrorCodeRange;
import com.nike.backstopper.handler.ApiExceptionHandlerUtils;
import com.nike.backstopper.handler.listener.ApiExceptionHandlerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public enum ProjectApiError implements ApiError {

    VALIDATION_ERROR(100, "a validation error has occurred", 400);

    private final ApiError delegate;

    ProjectApiError(ApiError delegate) {
        this.delegate = delegate;
    }

    ProjectApiError(ApiError delegate, Map<String, Object> metadata) {
        this(new ApiErrorWithMetadata(delegate, metadata));
    }

    ProjectApiError(Object errorCode, String message, int httpStatusCode) {
        this(new ApiErrorBase(
                "delegated-to-enum-wrapper-" + UUID.randomUUID(), String.valueOf(errorCode), message, httpStatusCode
        ));
    }

    ProjectApiError(Object errorCode, String message, int httpStatusCode, Map<String, Object> metadata) {
        this(new ApiErrorBase(
                "delegated-to-enum-wrapper-" + UUID.randomUUID(), String.valueOf(errorCode), message, httpStatusCode, metadata
        ));
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getErrorCode() {
        return delegate.getErrorCode();
    }

    @Override
    public String getMessage() {
        return delegate.getMessage();
    }

    @Override
    public int getHttpStatusCode() {
        return delegate.getHttpStatusCode();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return delegate.getMetadata();
    }

    @Named
    @Singleton
    public static class ProjectApiErrorsImpl extends NikeCommerceProjectApiErrorsBase {

        private static final List<ApiError> PROJECT_SPECIFIC_API_ERRORS = Arrays.asList(ProjectApiError.values());

        @Override
        protected List<ApiError> getProjectSpecificApiErrors() {
            return PROJECT_SPECIFIC_API_ERRORS;
        }

        @Override
        protected ProjectSpecificErrorCodeRange getProjectSpecificErrorCodeRange() {
            return ProjectSpecificErrorCodeRange.ALLOW_ALL_ERROR_CODES;
        }

    }

    @Configuration
    public static class ApplicationBackstopperOverrides {

        @Bean
        @Named("apiExceptionHandlerListenerCustomConfigurator")
        public Function<List<ApiExceptionHandlerListener>, List<ApiExceptionHandlerListener>> apiExceptionHandlerListenerCustomConfigurator() {
            return Function.identity();
        }

        @Bean
        @Named("apiExceptionHandlerUtilsCustomConfigurator")
        public Function<ApiExceptionHandlerUtils, ApiExceptionHandlerUtils> apiExceptionHandlerUtilsCustomConfigurator() {
            return Function.identity();
        }
    }
}
