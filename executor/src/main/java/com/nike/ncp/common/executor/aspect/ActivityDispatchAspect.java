package com.nike.ncp.common.executor.aspect;

import com.nike.ncp.common.executor.controller.ActivityDispatchControllerV1;
import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import com.nike.ncp.common.model.proxy.ActivityExecutionFailureRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord;
import com.nike.ncp.common.model.proxy.DispatchedActivity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.nike.ncp.common.model.ActivityExecutionStatusEnum.ACCEPTED;
import static com.nike.ncp.common.model.ActivityExecutionStatusEnum.FAILED;

@Slf4j
@Aspect
@Component
public class ActivityDispatchAspect {
    /**
     * Intercepts {@link ActivityDispatchControllerV1#putActivity(ObjectId, ObjectId, DispatchedActivity)}
     */
    @Pointcut(value = "activityDispatchInterface() && putActivityMethod()")
    public void activityDispatch() {}
    @Pointcut(value = "target(com.nike.ncp.common.executor.controller.ActivityDispatchControllerV1)")
    public void activityDispatchInterface() {}
    @Pointcut(value = "execution(org.springframework.http.ResponseEntity<com.nike.ncp.common.model.ActivityExecutionStatusEnum> *..putActivity(org.bson.types.ObjectId,org.bson.types.ObjectId,*))")
    public void putActivityMethod() {}

    /**
     * Populates logic around {@link ActivityDispatchControllerV1#putActivity(ObjectId, ObjectId, DispatchedActivity)}.
     * @return {@link ResponseEntity}&#60;{@link ActivityExecutionRecord}&#62;
     */
    @SuppressWarnings("unchecked")
    @Around(value = "activityDispatch()")
    public ResponseEntity<ActivityExecutionRecord> aroundActivityDispatch(ProceedingJoinPoint pjp) {
        final ActivityExecutionRecord.ActivityExecutionRecordBuilder<?, ?> recordBuilder =
                ActivityExecutionRecord.builder()
                        .beginTime(LocalDateTime.now()) // TODO timezone
                        .ecsTaskArn("ARN") // TODO
                        .privateIp("IP"); // TODO
        ResponseEntity<ActivityExecutionStatusEnum> proceed;
        ActivityExecutionStatusEnum activityStatus;

        try {
            proceed = (ResponseEntity<ActivityExecutionStatusEnum>) pjp.proceed();

            activityStatus = Objects.requireNonNull(
                    Objects.requireNonNull(proceed)
                            .getBody()
            );
            recordBuilder.status(activityStatus);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);

            ActivityExecutionFailureRecord failureRecord = ActivityExecutionFailureRecord.builder(
                    recordBuilder.status(FAILED).endTime(LocalDateTime.now()).build()
            ).failure(
                    ActivityExecutionFailureRecord.Failure.builder()
                            .message(e.getMessage())
                            .traceId(null) // TODO
                            .build()
            ).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failureRecord);
        }

        return ResponseEntity // TODO copy all properties
                .status(activityStatus == ACCEPTED ? HttpStatus.ACCEPTED : proceed.getStatusCode())
                .headers(proceed.getHeaders())
                .body(recordBuilder.build());
    }
}
