package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class ActivityExecutionRecord {
    @NonNull
    private final String privateIp;
    @NonNull
    private final String ecsTaskArn;
    @NonNull
    private final ActivityExecutionStatusEnum status;
    private final LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Throwable error;
}
