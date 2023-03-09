package com.nike.ncp.common.executor.aspect;

import com.nike.ncp.common.model.proxy.ActivityExecutionFailureRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.ACCEPTED;
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.DONE;
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.FAILED;
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.REJECTED;

@Slf4j
@Aspect
@Component
public class ActivityDispatchAspect {
    /**
     * Intercepts.
     */
    @Pointcut(value = "activityDispatchInterface() && putActivityMethod()")
    public void activityDispatch() {
    }
    @Pointcut(value = "target(com.nike.ncp.common.executor.controller.AbstractActivityDispatchController)")
    public void activityDispatchInterface() {
    }
    @Pointcut(value = "execution(org.springframework.http.ResponseEntity<com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum> *..putActivity(org.bson.types.ObjectId,org.bson.types.ObjectId,*))")
    public void putActivityMethod() {
    }

    /**
     * Populates logic around .
     * @return {@link ResponseEntity}&#60;{@link ActivityExecutionRecord}&#62;
     */
    @SuppressWarnings("unchecked")
    @Around(value = "activityDispatch()")
    public ResponseEntity<ActivityExecutionRecord> aroundActivityDispatch(ProceedingJoinPoint pjp) {
        // TODO turn ON scale-in protection. Use AWS SDK as needed.
            // mind the max 48-hour default effective period. Will need rollover strategy.

        final ActivityExecutionRecord.ActivityExecutionRecordBuilder<?, ?> recordBuilder =
                ActivityExecutionRecord.builder()
                        .beginTime(LocalDateTime.now()) // TODO timezone, ensure UTC everywhere, from code to DB
                        // TODO add container ARN, too?
                        .ecsTaskArn("ARN") // TODO retrieve from ECS container metadata
                        .privateIp("IP"); // TODO retrieve from ECS container metadata
        ResponseEntity<ActivityExecutionStatusEnum> proceed;
        ActivityExecutionStatusEnum activityStatus;

        try {
            proceed = (ResponseEntity<ActivityExecutionStatusEnum>) Objects.requireNonNull(pjp.proceed());
            // TODO more elegant and thorough checks
            activityStatus = proceed.getBody();
            if (null == activityStatus) {
                throw new UnsupportedOperationException();
            }

            recordBuilder.status(activityStatus);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);

            ActivityExecutionFailureRecord failureRecord = ActivityExecutionFailureRecord.builder(
                    recordBuilder.status(FAILED).endTime(LocalDateTime.now()).build()
            ).failure(
                    ActivityExecutionFailureRecord.Failure.builder()
                            .message(e.getMessage())
                            .traceId(null) // TODO distributed traceId, from wingtips?
                            .build()
            ).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failureRecord);
        }

        // TODO turn OFF scale-in protection. Use AWS SDK as needed.

        if (Arrays.asList(REJECTED, FAILED, DONE).contains(activityStatus)) {
            recordBuilder.endTime(LocalDateTime.now());
        }

        return ResponseEntity // TODO copy all properties
                .status(activityStatus == ACCEPTED ? HttpStatus.ACCEPTED : proceed.getStatusCode())
                .headers(proceed.getHeaders())
                .body(recordBuilder.build());
    }
}
