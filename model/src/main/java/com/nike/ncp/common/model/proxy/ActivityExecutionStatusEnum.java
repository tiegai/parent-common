package com.nike.ncp.common.model.proxy;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum ActivityExecutionStatusEnum {
    /**
     * When the Executor has successfully <b>accepted</b> an Activity for processing.
     */
    ACCEPTED(HttpStatus.ACCEPTED, false),
    /**
     * When an incoming request is illegal, malformed or unacceptable by the Executor in other ways,
     * usually due to a <b>client error</b> that causes Executor-side pre-check to fail.
     */
    REJECTED(HttpStatus.BAD_REQUEST, false),
    /**
     * When the Executor has previously received a same Activity, identified by its <a href="https://github.com/nike-gc-ncp/ncp-proxy/blob/8161c36d4cb2dec14b193f981c34c2759f372187/README.md#activity-uniqueness">uniqueness constraint</a>.
     * Such an Activity is meant to be processed for once only, unless otherwise allowed by its Executor.
     */
    DUPLICATED(HttpStatus.CONFLICT, false),
    /**
     * When the Executor has been <b>saturated</b> and unable to take in any additional Activity.
     */
    THROTTLED(HttpStatus.TOO_MANY_REQUESTS, true),
    /**
     * When the Executor has successfully accepted an Activity but <b>has not begun to process</b> it, yet.
     */
    PENDING(HttpStatus.CONTINUE, false),
    /**
     * When the Executor has begun to process the Activity, and it is <b>still in progress</b>.
     */
    RUNNING(HttpStatus.PROCESSING, false),
    /**
     * When the Executor has failed to complete an Activity due to an <b>Executor-side error or exception</b>.
     */
    FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false), // TODO there could be retryable failures, too
    /**
     * When the Executor has successfully <b>completed</b> an Activity.
     */
    DONE(HttpStatus.OK, false);

    /**
     * Optional but recommended HTTP status that best represents its enclosing {@link ActivityExecutionStatusEnum}.
     */
    private final HttpStatus httpStatus;
    /**
     * Whether <a href="https://github.com/nike-gc-ncp/ncp-common/blob/1254dd137cc5b12899ec95cb4c9f0ab679c328eb/executor/src/main/java/com/nike/ncp/common/executor/controller/ActivityDispatchControllerV1.java#L65">ActivityDispatchControllerV1#putActivity</a>
     * can be retried by <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>.
     */
    private final boolean retryable;

    ActivityExecutionStatusEnum(HttpStatus httpStatus, boolean retryable) {
        this.httpStatus = httpStatus;
        this.retryable = retryable;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public boolean isRetryable() {
        return retryable;
    }

    @Nullable
    public static ActivityExecutionStatusEnum getBy(HttpStatus httpStatus) {
        return Arrays.stream(ActivityExecutionStatusEnum.values())
                .filter(e -> httpStatus.equals(e.getHttpStatus()))
                .findAny().orElse(null);
    }
}
