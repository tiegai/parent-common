package com.nike.ncp.common.executor.controller;

import com.nike.ncp.common.executor.model.ActivityExecutionResult;
import com.nike.ncp.common.executor.service.CommonActivityDispatchService;
import com.nike.ncp.common.executor.service.ProxyFeedbackService;
import com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum;
import com.nike.ncp.common.model.proxy.ActivityFeedbackEssentials;
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
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.REJECTED;
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.THROTTLED;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActivityDispatchController<ACTIVITY_CONFIG, CHECKED_DATA> extends AbstractActivityDispatchController<ACTIVITY_CONFIG> {

    private final transient ThreadPoolExecutor threadPoolExecutor;
    private final transient CommonActivityDispatchService<CHECKED_DATA> activityDispatchService;
    private final transient ProxyFeedbackService proxyFeedbackService;

    public ResponseEntity<ActivityExecutionStatusEnum> putActivity(
            @PathVariable ObjectId journeyInstanceId,
            @PathVariable ObjectId activityId,
            @RequestBody DispatchedActivity<ACTIVITY_CONFIG> activityPayload
    ) {
        log("Executor receive activity", activityPayload);
        // check on business level
        ActivityExecutionResult<CHECKED_DATA> preCheckResult;
        try {
            preCheckResult = activityDispatchService.preCheck(activityPayload);
        } catch (Exception e) {
            log("Executor reject activity", activityPayload, e);
            return new ResponseEntity<>(REJECTED, REJECTED.getHttpStatus());
        }
        if (preCheckResult.getFailure() != null) {
            log("Executor reject activity", activityPayload, preCheckResult.getFailure());
            return new ResponseEntity<>(REJECTED, REJECTED.getHttpStatus());
        }
        // execute task
        try {
            threadPoolExecutor.execute(
                    () -> {
                        DispatchedActivity.Activity<?> activity = activityPayload.getActivity();
                        DispatchedActivity.Journey journey = activityPayload.getJourney();
                        ActivityFeedbackEssentials feedbackEssentials = new ActivityFeedbackEssentials(
                                journey.getInstanceId(),
                                journey.getDefinitionId(),
                                activity.getId(),
                                activityPayload.getActivity().getCategory()
                        );
                        ActivityExecutionResult<?> executionResult;
                        try {
                            executionResult = activityDispatchService.execute(activityPayload, preCheckResult.getData());
                        } catch (Exception e) {
                            proxyFeedbackService.failure(feedbackEssentials, e).cache().subscribe();
                            return;
                        }
                        if (executionResult.getFailure() != null) {
                            proxyFeedbackService.failure(feedbackEssentials, executionResult.getFailure()).cache().subscribe();
                        } else {
                            proxyFeedbackService.success(feedbackEssentials).cache().subscribe();
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
