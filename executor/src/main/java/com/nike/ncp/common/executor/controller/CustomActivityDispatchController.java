package com.nike.ncp.common.executor.controller;

import com.nike.ncp.common.executor.model.ActivityExecutionResult;
import com.nike.ncp.common.executor.service.CustomActivityDispatchService;
import com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum;
import com.nike.ncp.common.model.proxy.DispatchedActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.ACCEPTED;
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.THROTTLED;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomActivityDispatchController<ACTIVITY_CONFIG, CHECKED_DATA> extends AbstractActivityDispatchController<ACTIVITY_CONFIG> {

    private final transient ThreadPoolExecutor threadPoolExecutor;
    private final transient CustomActivityDispatchService<CHECKED_DATA> activityDispatchService;

    public ResponseEntity<ActivityExecutionStatusEnum> putActivity(
            @PathVariable ObjectId journeyInstanceId,
            @PathVariable ObjectId activityId,
            @RequestBody DispatchedActivity<ACTIVITY_CONFIG> activityPayload
    ) {
        log("Executor receive activity", activityPayload);
        ActivityExecutionResult<CHECKED_DATA> preCheckResult = preCheck(activityPayload, activityDispatchService::preCheck);
        if (preCheckResult.getStatusEnum() != ACCEPTED) {
            return new ResponseEntity<>(preCheckResult.getStatusEnum(), preCheckResult.getStatusEnum().getHttpStatus());
        }
        // execute task
        try {
            threadPoolExecutor.execute(
                    () -> {
                        try {
                            activityDispatchService.execute(activityPayload, preCheckResult.getData());
                        } catch (Exception e) {
                            log.error("Executor error : " + e.getMessage(), e);
                        }
                    }

            );
        } catch (RejectedExecutionException e) {
            log("Executor reject activity", activityPayload, e);
            return new ResponseEntity<>(THROTTLED, THROTTLED.getHttpStatus());
        }
        log("Executor accept activity", activityPayload);
        return new ResponseEntity<>(ACCEPTED, ACCEPTED.getHttpStatus());
    }
}
