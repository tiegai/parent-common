package com.nike.ncp.common.utilities.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiExceptionResponse {

    private String appName;
    @Builder.Default
    private String errorId = UUID.randomUUID().toString();
    private List<ApiErrorMessage> errors;
}
