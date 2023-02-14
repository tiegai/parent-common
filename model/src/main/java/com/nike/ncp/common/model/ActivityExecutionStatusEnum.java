package com.nike.ncp.common.model;

import org.springframework.http.HttpStatus;

public enum ActivityExecutionStatusEnum {
    /**
     * When the Executor has successfully <b>accepted</b> an Activity for processing.
     */
    ACCEPTED(HttpStatus.ACCEPTED),
    /**
     * When an incoming request is illegal, malformed or unacceptable by the Executor in other ways,
     * usually due to a <b>client error</b> that causes Executor-side pre-check to fail.
     */
    REJECTED(HttpStatus.BAD_REQUEST),
    /**
     * When the Executor has been <b>saturated</b> and unable to take in any additional Activity.
     */
    THROTTLED(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED),
    /**
     * When the Executor has successfully accepted an Activity but <b>has not begun to process</b> it, yet.
     */
    PENDING(HttpStatus.CONTINUE),
    /**
     * When the Executor has begun to process the Activity, and it is <b>still in progress</b>.
     */
    RUNNING(HttpStatus.PROCESSING),
    /**
     * When the Executor has failed to complete an Activity due to an <b>Executor-side error or exception</b>.
     */
    FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    /**
     * When the Executor has successfully <b>completed</b> an Activity.
     */
    DONE(HttpStatus.OK);

    /**
     * Recommended HTTP status that best represents its enclosing {@link ActivityExecutionStatusEnum}.
     */
    private final HttpStatus httpStatus;

    ActivityExecutionStatusEnum(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
