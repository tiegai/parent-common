package com.nike.ncp.common.executor.model;

import com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum;
import lombok.Data;

@Data
public final class ActivityExecutionResult<T> {
    private ActivityExecutionStatusEnum statusEnum;
    private Throwable failure;
    private T data;

    private ActivityExecutionResult() {
    }

    public static <T> ActivityExecutionResult<T> failure(Throwable failure) {
        ActivityExecutionResult<T> result = new ActivityExecutionResult<>();
        result.setFailure(failure);
        result.setStatusEnum(ActivityExecutionStatusEnum.REJECTED);
        return result;
    }

    public static <T> ActivityExecutionResult<T> failure(Throwable failure, ActivityExecutionStatusEnum statusEnum) {
        ActivityExecutionResult<T> result = new ActivityExecutionResult<>();
        result.setFailure(failure);
        result.setStatusEnum(statusEnum);
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
