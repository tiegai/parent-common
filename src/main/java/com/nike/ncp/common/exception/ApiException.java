package com.nike.ncp.common.exception;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 5038988837966289350L;

    @Builder.Default
    private List<ApiErrorMessage> errors = Lists.newArrayList();
    private HttpStatus status;

    public ApiException with(int code, String message) {
        errors.add(ApiErrorMessage.builder().code(code).message(message).build());
        return this;
    }
}
