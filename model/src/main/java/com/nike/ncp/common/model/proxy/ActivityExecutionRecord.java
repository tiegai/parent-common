package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class ActivityExecutionRecord {
    private final String privateIp;
    private final String ecsTaskArn;
    private final String executionBeginTime;
    private final ActivityExecutionStatusEnum status;
    private Throwable error;
}
