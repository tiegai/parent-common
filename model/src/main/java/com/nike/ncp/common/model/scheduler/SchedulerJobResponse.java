package com.nike.ncp.common.model.scheduler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class SchedulerJobResponse {
    private String journeyId;
    private String nextStartTime;
}
