package com.nike.ncp.common.executor.service;

import com.nike.ncp.common.executor.properties.CommonExecutorProperties;
import com.nike.ncp.common.executor.properties.EcsMetadata;
import com.nike.ncp.common.model.proxy.ActivityExecutionFailureRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionRecord.ActivityExecutionRecordBuilder;
import com.nike.ncp.common.model.proxy.ActivityFailureFeedbackRequest;
import com.nike.ncp.common.model.proxy.ActivityFeedbackEssentials;
import com.nike.ncp.common.model.proxy.ActivityFeedbackRequest;
import com.nike.ncp.common.model.proxy.ActivityFeedbackRequest.ActivityFeedbackRequestBuilder;
import com.nike.wingtips.util.TracingState;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import javax.annotation.Resource;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.DONE;
import static com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum.FAILED;
import static com.nike.wingtips.util.AsyncWingtipsHelperStatic.biFunctionWithTracing;
import static com.nike.wingtips.util.AsyncWingtipsHelperStatic.consumerWithTracing;

@Slf4j
@Service
public class ProxyFeedbackService {
    /**
     * The maximum number of retries. If uncertain, leave a dummy placeholder like below in your {@code application.properties}.
     * <pre class="code">
     * ncp.proxy.feedback.max.retries=
     * </pre>
     * Default is {@link Long#MAX_VALUE}, which emulates indefinite retries.
     * <p/>
     * In case of retry exhaustion, instead of the {@link ProxyFeedbackService#success} or {@link ProxyFeedbackService#failure} "shortcuts",
     * it is <b>HIGHLY RECOMMENDED</b> to use {@link ProxyFeedbackService#success(ActivityFeedbackEssentials, BiFunction)}
     * or {@link ProxyFeedbackService#failure(ActivityFeedbackEssentials, Throwable, BiFunction)} to supply your own handler
     * other than {@link ProxyFeedbackService#getDefaultRetryExhaustionHandler()}.
     */
    @Value("${ncp.proxy.feedback.max.retries}")
    private Long feedbackMaxRetries;
    @Value("${ncp.proxy.feedback.retry.interval:2}")
    private int feedbackRetryInterval;

    @Resource
    private CommonExecutorProperties properties;
    @Resource
    @Qualifier(value = "commonExecutorWebClient")
    private WebClient commonExecutorWebClient;

    /**
     * Feed back a success to <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>.
     * @param essentials Essential information about this feedback
     * @return Bodiless {@link Mono}&#60;{@link ResponseEntity}&#62;
     */
    @SuppressWarnings("unused")
    public Mono<ResponseEntity<Void>> success(@NonNull ActivityFeedbackEssentials essentials) {
        return this.success(essentials, null);
    }

    /**
     * Feed back a failure to <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>.
     * @param essentials Essential information about this feedback
     * @param failure What went wrong with the activity
     * @return Bodiless {@link Mono}&#60;{@link ResponseEntity}&#62;
     */
    @SuppressWarnings("unused")
    public Mono<ResponseEntity<Void>> failure(
            @NonNull ActivityFeedbackEssentials essentials,
            @NonNull Throwable failure
    ) {
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
            @NonNull ActivityFeedbackEssentials essentials,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        String text = "Executor success feedback [journeyDefinitionId={}], [journeyInstanceId={}], [activityId={}], [category={}].";
        log.info(text, essentials.getJourneyDefinitionId(), essentials.getJourneyInstanceId(), essentials.getActivityId(), essentials.getActivityCategory().value());
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
            @NonNull ActivityFeedbackEssentials essentials,
            @NonNull Throwable failure,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        String text = "Executor failure feedback [journeyDefinitionId={}], [journeyInstanceId={}], [activityId={}], [category={}]. message: {}";
        log.error(text, essentials.getJourneyDefinitionId(), essentials.getJourneyInstanceId(), essentials.getActivityId(), essentials.getActivityCategory().value(), failure.getMessage(), failure);
        return this.feedBack(essentials, failure, retryExhaustionHandler);
    }

    private Mono<ResponseEntity<Void>> feedBack(
            @NonNull ActivityFeedbackEssentials essentials,
            Throwable throwable,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        ActivityExecutionRecordBuilder<?, ?> executionRecordBuilder = getExecutionRecordBuilder();
        ActivityFeedbackRequestBuilder<?, ?> requestBuilder = getFeedbackRequestBuilder(essentials, executionRecordBuilder);

        if (null != throwable) { // this is not a success but a failure feedback
            final ActivityExecutionFailureRecord.Failure failure = ActivityExecutionFailureRecord.Failure.builder()
                    .message(throwable.getMessage())
                    .traceId(MDC.get("traceId"))
                    .build();

            requestBuilder = ActivityFailureFeedbackRequest.builder(requestBuilder.build(), FAILED, failure);
        }

        final ActivityFeedbackRequest feedbackRequest = requestBuilder.build();
        final Class<?> bodyClass = feedbackRequest instanceof ActivityFailureFeedbackRequest
                ? ActivityFailureFeedbackRequest.class
                : ActivityFeedbackRequest.class;

        final String host = properties.getProxyHostUrl();
        final Function<UriBuilder, URI> pathFunc = uriBuilder -> uriBuilder.path(
                        null == throwable ? properties.getSuccessFeedbackPath() : properties.getFailureFeedbackPath()
                ).build(essentials.getJourneyInstanceId(), essentials.getActivityId());

        return commonExecutorWebClient
                .post()
                .uri(host, pathFunc)
                .body(Mono.just(feedbackRequest), bodyClass)
                /*
                  traceId outbound: propagate tracing to downstream
                  https://github.com/Nike-Inc/wingtips/tree/main/wingtips-spring-webflux#register-a-wingtipsspringwebfluxexchangefilterfunction-for-automatic-tracing-propagation-when-using-springs-reactive-webclient
                 */
                .attribute(TracingState.class.getName(), TracingState.getCurrentThreadTracingState())
                // network exchange
                .retrieve()
                .toBodilessEntity()
                // success/error handling
                .doOnSuccess(consumerWithTracing(r -> log.info("{} succeeded, status={}, feedback={}",
                        bodyClass.getName(), r.getStatusCode(), feedbackRequest)
                )).doOnError(consumerWithTracing(r -> log.error("{} failed, reason={}, feedback={}",
                        bodyClass.getName(), r.getMessage(), feedbackRequest)
                )).onErrorResume(Mono::error)
                // retry strategy
                .retryWhen(getDefaultRetrySpec(feedbackRequest, retryExhaustionHandler))
                // https://stackoverflow.com/a/59286752
                .cache();
    }

    private static ActivityFeedbackRequestBuilder<?, ?> getFeedbackRequestBuilder(
            @NonNull ActivityFeedbackEssentials essentials,
            @NonNull ActivityExecutionRecordBuilder<?, ?> executionRecordBuilder
    ) {
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
                .ecsTaskArn(EcsMetadata.getLabels().get("com.amazonaws.ecs.task-arn"))
                .privateIp(EcsMetadata.getNetworks().get(0).getIPv4Addresses().get(0))
                .status(DONE);
    }

    private RetryBackoffSpec getDefaultRetrySpec(
            @NonNull ActivityFeedbackRequest feedbackRequest,
            BiFunction<RetryBackoffSpec, Retry.RetrySignal, Throwable> retryExhaustionHandler
    ) {
        return Retry.backoff(
                        Objects.requireNonNullElse(feedbackMaxRetries, Long.MAX_VALUE),
                        Duration.ofSeconds(feedbackRetryInterval)
                ).jitter(0.5d)
                .doBeforeRetry(consumerWithTracing(s -> log.warn("Retrying({}/{}) {}, reason={}, feedback={}",
                        s.totalRetries() + 1, feedbackMaxRetries,
                        feedbackRequest.getClass().getName(),
                        s.failure().getMessage(),
                        feedbackRequest)
                )).onRetryExhaustedThrow(biFunctionWithTracing(Objects.requireNonNullElse(
                        retryExhaustionHandler,
                        getDefaultRetryExhaustionHandler()
                )));
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
