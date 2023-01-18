package com.nike.ncp.common.executor.service;

import com.nike.ncp.common.executor.properties.CommonExecutorProperties;
import com.nike.ncp.common.model.proxy.ActivityExecutionFailureRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord.ActivityExecutionRecordBuilder;
import com.nike.ncp.common.model.proxy.ActivityFailureFeedbackRequest;
import com.nike.ncp.common.model.proxy.ActivityFeedbackEssentials;
import com.nike.ncp.common.model.proxy.ActivityFeedbackRequest;
import com.nike.ncp.common.model.proxy.ActivityFeedbackRequest.ActivityFeedbackRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiFunction;

import static com.nike.ncp.common.model.ActivityExecutionStatusEnum.DONE;
import static com.nike.ncp.common.model.ActivityExecutionStatusEnum.FAILED;

@Slf4j
@Service
public class ProxyFeedbackService {
    /**
     * e.g. http(s)://internal-onencp-proxy-alb-428960147.cn-northwest-1.elb.amazonaws.com.cn <br/>
     *      &emsp;&emsp;http(s)://proxy.onencp-test.gcncp.nikecloud.com.cn
     */
    @Value("${ncp.proxy.base.url}")
    private String proxyUrl;
    /**
     * The maximum number of retries. If uncertain, leave a dummy placeholder like below in your {@code application.properties}.
     * <pre class="code">
     * ncp.proxy.feedback.max.retries=
     * </pre>
     * Default is {@link Long#MAX_VALUE}, which emulates indefinite retries.
     * <p/>
     * When calling {@link ProxyFeedbackService#success} or {@link ProxyFeedbackService#failure}, in case of retry exhaustion,
     * it is also highly recommended to provision your own handler other than {@link ProxyFeedbackService#getDefaultRetryExhaustionHandler()}.
     */
    @Value("${ncp.proxy.feedback.max.retries}")
    private Long feedbackMaxRetries;
    @Value("${ncp.proxy.feedback.retry.interval:2}")
    private int feedbackRetryInterval;

    @Resource
    private CommonExecutorProperties properties;

    /**
     * Feed back a success to <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>.
     * @param essentials Essential information about this feedback
     * @return Bodiless {@link Mono}&#60;{@link ResponseEntity}&#62;
     */
    @SuppressWarnings("unused")
    public Mono<ResponseEntity<Void>> success(ActivityFeedbackEssentials essentials) {
        return this.success(essentials, null);
    }

    /**
     * Feed back a failure to <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>.
     * @param essentials Essential information about this feedback
     * @param failure What went wrong with the activity
     * @return Bodiless {@link Mono}&#60;{@link ResponseEntity}&#62;
     */
    @SuppressWarnings("unused")
    public Mono<ResponseEntity<Void>> failure(ActivityFeedbackEssentials essentials, Throwable failure) {
        return this.failure(essentials, failure, null);
    }

    /**
     * Feed back a success to <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>
     * with a custom {@code retryExhaustionHandler}.
     * @param essentials Essential information about this feedback
     * @param retryExhaustionHandler What to do when all retry attempts were unsuccessful
     * @return Bodiless {@link Mono}&#60;{@link ResponseEntity}&#62;
     */
    public Mono<ResponseEntity<Void>> success(
            ActivityFeedbackEssentials essentials,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        return this.feedBack(essentials, null, retryExhaustionHandler);
    }

    /**
     * Feed back a failure to <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>
     * with a custom {@code retryExhaustionHandler}.
     * @param essentials Essential information about this feedback
     * @param failure What went wrong with the activity
     * @param retryExhaustionHandler What to do when all retry attempts were unsuccessful
     * @return Bodiless {@link Mono}&#60;{@link ResponseEntity}&#62;
     */
    public Mono<ResponseEntity<Void>> failure(
            ActivityFeedbackEssentials essentials,
            Throwable failure,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        return this.feedBack(essentials, failure, retryExhaustionHandler);
    }

    private Mono<ResponseEntity<Void>> feedBack(
            ActivityFeedbackEssentials essentials,
            Throwable failure,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        ActivityExecutionRecordBuilder<?, ?> executionRecordBuilder = getExecutionRecordBuilder();
        ActivityFeedbackRequestBuilder<?, ?> requestBuilder = getFeedbackRequestBuilder(essentials, executionRecordBuilder);

        if (null != failure) { // this is not a success but a failure feedback
            executionRecordBuilder = ActivityExecutionFailureRecord.builder(executionRecordBuilder.build())
                    .status(FAILED)
                    .failure(ActivityExecutionFailureRecord.Failure.builder()
                            .message(failure.getMessage())
                            .traceId(null) // TODO distributed traceId, from wingtips?
                            .build()
                    );

            requestBuilder = ActivityFailureFeedbackRequest.builder(requestBuilder.build())
                    .executionRecord((ActivityExecutionFailureRecord) executionRecordBuilder.build());
        }

        final ActivityFeedbackRequest feedbackRequest = requestBuilder.build();
        final Class<?> bodyClass = feedbackRequest instanceof ActivityFailureFeedbackRequest
                ? ActivityFailureFeedbackRequest.class
                : ActivityFeedbackRequest.class;

        return WebClient.create(proxyUrl)
                .post()
                .uri(uriBuilder -> uriBuilder.path(
                    null == failure
                        ? properties.getSuccessFeedbackPath()
                        : properties.getFailureFeedbackPath())
                    .build(essentials.getJourneyInstanceId(), essentials.getActivityId())
                ).body(Mono.just(feedbackRequest), bodyClass)
                // network exchange
                .retrieve()
                .toBodilessEntity()
                // success/error handling
                .doOnSuccess(r -> log.info("isComplete=true, status={}, method={}, feedback={}",
                        r.getStatusCode(),
                        essentials.getClass().getEnclosingMethod().getName(),
                        feedbackRequest)
                ).doOnError(r -> log.error("isComplete=false reason={}, method={}, feedback={}",
                        r.getMessage(),
                        essentials.getClass().getEnclosingMethod().getName(),
                        feedbackRequest)
                ).onErrorResume(Mono::error)
                // retry strategy
                .retryWhen(getDefaultRetrySpec(essentials, feedbackRequest, retryExhaustionHandler));
    }

    private static ActivityFeedbackRequestBuilder<?, ?> getFeedbackRequestBuilder(ActivityFeedbackEssentials essentials, ActivityExecutionRecordBuilder<?, ?> executionRecordBuilder) {
        return ActivityFeedbackRequest.builder()
                .journeyInstanceId(essentials.getJourneyInstanceId())
                .journeyDefinitionId(essentials.getJourneyDefinitionId())
                .activityId(essentials.getActivityId())
                .activityCategory(essentials.getActivityCategory())
                .executionRecord(executionRecordBuilder.build());
    }

    private static ActivityExecutionRecordBuilder<?, ?> getExecutionRecordBuilder() {

        return ActivityExecutionRecord.builder()
                .endTime(LocalDateTime.now()) // TODO timezone, ensure UTC everywhere, from code to DB
                // TODO add container ARN, too?
                .ecsTaskArn("DUMMY_ARN") // TODO retrieve from ECS container metadata
                .privateIp("DUMMY_IP") // TODO retrieve from ECS container metadata
                .status(DONE);
    }

    private RetryBackoffSpec getDefaultRetrySpec(
            ActivityFeedbackEssentials essentials,
            ActivityFeedbackRequest feedbackRequest,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        return Retry.backoff(
                        Objects.requireNonNullElse(feedbackMaxRetries, Long.MAX_VALUE),
                        Duration.ofSeconds(feedbackRetryInterval)
                ).jitter(0.5d)
                .doBeforeRetry(s -> log.warn("Retrying({}/{}), reason={}, method={}, feedback={}",
                        s.totalRetries() + 1, feedbackMaxRetries,
                        s.failure().getMessage(),
                        essentials.getClass().getEnclosingMethod().getName(),
                        feedbackRequest)
                ).onRetryExhaustedThrow(Objects.requireNonNullElse(
                        retryExhaustionHandler,
                        getDefaultRetryExhaustionHandler()
                ));
    }

    /**
     * @return {@link BiFunction} as the parameter for {@link RetryBackoffSpec#onRetryExhaustedThrow(BiFunction)}
     */
    private static BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> getDefaultRetryExhaustionHandler() {
        return (spec, signal) -> {
            return new Error(signal.failure()); // TODO custom error class
        };
    }
}
