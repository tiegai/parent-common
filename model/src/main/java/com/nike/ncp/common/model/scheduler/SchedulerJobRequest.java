package com.nike.ncp.common.model.scheduler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class SchedulerJobRequest {
    private String journeyId;
    private String description;
    private String periodicBegin;
    private String periodicEnd;
    private String periodicType;
    private String periodicValues;
    private String periodicTimes;
    private String nextStartTime;
    private String createdTime;
    private String modifiedTime;
}
