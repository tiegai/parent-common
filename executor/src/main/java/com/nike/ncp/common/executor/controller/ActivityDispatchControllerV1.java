package com.nike.ncp.common.executor.controller;

import com.nike.ncp.common.executor.aspect.ActivityDispatchAspect;
import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import com.nike.ncp.common.model.journey.AudienceConfig;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord;
import com.nike.ncp.common.model.proxy.DispatchedActivity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implement this interface to allow seamless API exchanges
 * between <a href="https://github.com/nike-gc-ncp/ncp-proxy">One-NCP proxy</a>
 * and your One-NCP executor. Use below example for reference:
 * <p/>
 * <pre class="code">
 * &#64;{@link RestController}
 * public class ConcreteController implements {@link ActivityDispatchControllerV1}&#60;{@link AudienceConfig}&#62; {
 *      &#64;Override
 *      public {@link ResponseEntity}<{@link ActivityExecutionStatusEnum}> putActivity(
 *          &#64;{@link PathVariable} String journeyInstanceId,
 *          &#64;{@link PathVariable} String activityId,
 *          &#64;{@link RequestBody} {@link DispatchedActivity}&#60;{@link AudienceConfig}&#62; activityPayload
 *      ) {
 *          // do everything you need to do, ideally asynchronously
 *
 *          // return an {@link ActivityExecutionStatusEnum} to indicate your decision about this {@link DispatchedActivity}.
 *          return {@link ResponseEntity}
 *                  .status({@link HttpStatus})
 *                  .body({@link ActivityExecutionStatusEnum})
 *                  .build();
 *      }
 * }
 * </pre>
 * Learn more about how <a href="https://www.baeldung.com/spring-interface-driven-controllers#2-interface">interface-driven controller</a> works.
 *
 * @param <CONFIG_TYPE> Data type of {@link DispatchedActivity.Activity#getConfig()}. <br/>
 *                     Consult authors of <a href="https://github.com/nike-gc-ncp/ncp-journeyengine">ncp-journeyengine</a>
 *                     for a list of available {@code CONFIG_TYPE}s.
 */
@RestController
@RequestMapping("/v1")
public interface ActivityDispatchControllerV1<CONFIG_TYPE> {
    /**
     * Accepts and processes a {@link DispatchedActivity} sent from <a href="https://github.com/nike-gc-ncp/ncp-proxy">One-NCP proxy</a>.
     * <br/>
     * Ideally, this method should return as quickly as possible and asynchronously finish the processing work.
     *
     * @param journeyInstanceId Journey's instance ID
     * @param activityId Journey activity's ID
     * @param activityPayload Journey activity's payload
     * @return Ideally, {@link ResponseEntity}&#60;{@link ActivityExecutionStatusEnum#ACCEPTED} || {@link ActivityExecutionStatusEnum#REJECTED} || {@link ActivityExecutionStatusEnum#THROTTLED} || {@link ActivityExecutionStatusEnum#FAILED}&#62; <br/><br/>
     * After returned, {@link ActivityDispatchAspect#aroundActivityDispatch(ProceedingJoinPoint)} will intercept and
     * convert this return value into a {@link ResponseEntity}&#60;{@link ActivityExecutionRecord}&#62;,
     * and return to <a href="https://github.com/nike-gc-ncp/ncp-proxy">One-NCP proxy</a>.
     */
    @PutMapping("/journeyInstance/{journeyInstanceId}/activity/{activityId}")
    ResponseEntity<ActivityExecutionStatusEnum> putActivity(
            @PathVariable ObjectId journeyInstanceId,
            @PathVariable ObjectId activityId,
            @RequestBody DispatchedActivity<CONFIG_TYPE> activityPayload
    );
}
