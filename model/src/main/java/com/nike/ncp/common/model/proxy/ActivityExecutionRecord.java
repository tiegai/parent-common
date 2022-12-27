package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class ActivityExecutionRecord {
    private final String privateIp;
    private final String ecsTaskArn;
    private final Date timestamp;
    private final ActivityExecutionStatusEnum status;
    private Throwable error;
}
