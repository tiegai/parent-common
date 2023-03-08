package com.nike.ncp.common.executor.service;

import com.nike.ncp.common.executor.model.ActivityExecutionResult;
import com.nike.ncp.common.model.proxy.DispatchedActivity;

public interface CustomActivityDispatchService<CHECKED_DATA> {

    ActivityExecutionResult<CHECKED_DATA> preCheck(
            DispatchedActivity<?> activityPayload
    );

    void execute(
            DispatchedActivity<?> activityPayload,
            CHECKED_DATA checkedData
    );


}
