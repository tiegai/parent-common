package com.nike.ncp.common.utilities.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 5038988837966289350L;

    @Builder.Default
    private List<ApiErrorMessage> errors = new ArrayList<>();
    private HttpStatus status;
    @Builder.Default
    private boolean warningMessage = false;

    public ApiException with(int code, String message) {
        errors.add(ApiErrorMessage.builder().code(code).message(message).build());
        return this;
    }

    public ApiException warning() {
        warningMessage = true;
        return this;
    }
}
