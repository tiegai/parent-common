package com.nike.ncp.common.model.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class ActivityExecutionFailureRecord extends ActivityExecutionRecord {
    @NonNull
    private Failure failure;

    /**
     * Declaring {@link ActivityExecutionFailureRecord#builder(ActivityExecutionRecord)}
     * without this no-args {@link ActivityExecutionFailureRecord#builder()}
     * will fall back {@code ActivityExecutionFailureRecord.builder()} invocations to its super {@link ActivityExecutionRecord#builder()},
     * in which case {@link ActivityExecutionFailureRecordBuilder#failure(Failure)} will no longer be visible to you,
     * thus can no longer provision a {@link Failure}.
     * <br/>
     * This may just be how <a href="https://projectlombok.org/features/experimental/SuperBuilder">Lombok</a>'s @{@link SuperBuilder}
     * "undesirably" works.
     * @return no-args {@link ActivityExecutionFailureRecord#builder()}
     */
    @SuppressWarnings("JavadocDeclaration")
    public static ActivityExecutionFailureRecordBuilder<?, ?> builder() {
        return new ActivityExecutionFailureRecordBuilderImpl();
    }

    /**
     * Creates a {@link ActivityExecutionFailureRecordBuilder} by a {@link ActivityExecutionRecord}.
     * @param executionRecord {@link ActivityExecutionRecord}
     * @return {@link ActivityExecutionFailureRecordBuilder}
     */
    public static ActivityExecutionFailureRecordBuilder<?, ?> builder(ActivityExecutionRecord executionRecord) {
        return null == executionRecord ? new ActivityExecutionFailureRecordBuilderImpl()
                : new ActivityExecutionFailureRecordBuilderImpl()
                .privateIp(executionRecord.getPrivateIp())
                .ecsTaskArn(executionRecord.getEcsTaskArn())
                .status(executionRecord.getStatus())
                .beginTime(executionRecord.getBeginTime())
                .endTime(executionRecord.getEndTime());
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Failure {
        /**
         * A message that clearly summarizes what went wrong.
         * <br/>
         * Ideally, {@link Throwable#getMessage()}.
         */
        @NonNull
        private String message;
        /**
         * A distributed trace ID that facilitates log analysis and troubleshooting.
         */
        private String traceId;
    }
}
