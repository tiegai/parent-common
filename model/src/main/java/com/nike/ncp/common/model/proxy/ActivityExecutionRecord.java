package com.nike.ncp.common.model.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(doNotUseGetters = true)
public class ActivityExecutionRecord {
    @NonNull
    private String privateIp;
    @NonNull
    private String ecsTaskArn;
    @NonNull
    private ActivityExecutionStatusEnum status;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
