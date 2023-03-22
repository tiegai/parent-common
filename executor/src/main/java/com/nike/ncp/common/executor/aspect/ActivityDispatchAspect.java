package com.nike.ncp.common.executor.aspect;

import com.nike.ncp.common.executor.properties.EcsEnvMetaData;
import com.nike.ncp.common.executor.service.EcsScaleInProtectionService;
import com.nike.ncp.common.model.proxy.ActivityExecutionFailureRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    @Resource
    private EcsScaleInProtectionService ecsScaleInProtectionService;

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
        // mind the max 48-hour effective period default 2-hour. Will need rollover strategy.
        ecsScaleInProtectionService.scaleInProtection("Enable", true);
        final ActivityExecutionRecord.ActivityExecutionRecordBuilder<?, ?> recordBuilder =
                ActivityExecutionRecord.builder()
                        .beginTime(LocalDateTime.now()) // TODO timezone, ensure UTC everywhere, from code to DB
                        .ecsTaskArn(EcsEnvMetaData.getContainerARN())
                        .privateIp(EcsEnvMetaData.getNetworks().get(0).getIPv4Addresses().get(0));
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
                            .traceId(MDC.get("traceId"))
                            .build()
            ).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failureRecord);
        }

        if (Arrays.asList(REJECTED, FAILED, DONE).contains(activityStatus)) {
            recordBuilder.endTime(LocalDateTime.now());
        }

        return ResponseEntity // TODO copy all properties
                .status(activityStatus == ACCEPTED ? HttpStatus.ACCEPTED : proceed.getStatusCode())
                .headers(proceed.getHeaders())
                .body(recordBuilder.build());
    }
}
