package com.nike.ncp.common.executor.model;

import lombok.Data;

@Data
public final class ActivityExecutionResult<T> {
    private Throwable failure;
    private T data;

    private ActivityExecutionResult() {
    }

    public static <T> ActivityExecutionResult<T> failure(Throwable failure) {
        ActivityExecutionResult<T> result = new ActivityExecutionResult<>();
        result.setFailure(failure);
        return result;
    }

    public static <T> ActivityExecutionResult<T> success(T data) {
        ActivityExecutionResult<T> result = new ActivityExecutionResult<>();
        result.setData(data);
        return result;
    }

    public static ActivityExecutionResult<Void> success() {
        return new ActivityExecutionResult<>();
    }
}
