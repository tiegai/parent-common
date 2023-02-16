package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Sample {@link ActivityFailureFeedbackRequest} in JSON representation.
 * <pre class="code">
 * {
 *     "journeyInstanceId": "63998f7c958e0e77f20ed8ee",
 *     "activityId": "63998f7c958e0e77f20ed8ee",
 *     "activityCategory": "FREQ_CTRL",
 *     "executionRecord": {
 *         "privateIp": "127.0.0.2",
 *         "ecsTaskArn": "arn:aws-cn:ecs:cn-northwest-1:AWS_ACCOUNT_NO:task/onencp-test-cluster/1b73182c9229458b81044c9f9ef60645",
 *         "status": "DONE",
 *         "failure": {
 *             "message": "io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379",
 *             "traceId": "8fe21e2d94fc43ee"
 *         }
 *     }
 * }
 * </pre>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class ActivityFailureFeedbackRequest extends ActivityFeedbackRequest {

    @Override
    public ActivityExecutionFailureRecord getExecutionRecord() {
        return (ActivityExecutionFailureRecord) super.getExecutionRecord();
    }

    public void setExecutionRecord(@NonNull ActivityExecutionFailureRecord executionRecord) {
        super.setExecutionRecord(executionRecord);
    }

    /**
     * Please use {@link ActivityFailureFeedbackRequest#setExecutionRecord(ActivityExecutionFailureRecord)}, instead.
     */
    @Deprecated
    @Override
    public void setExecutionRecord(@NonNull ActivityExecutionRecord executionRecord) {
        if (executionRecord instanceof ActivityExecutionFailureRecord) {
            this.setExecutionRecord((ActivityExecutionFailureRecord) executionRecord);
        } else {
            throw new UnsupportedOperationException(String.format("Expected type: %s. Actual type: %s",
                    ActivityExecutionFailureRecord.class.getName(), executionRecord.getClass().getName()));
        }
    }

    /**
     * OOP-wise, similar to {@link ActivityExecutionFailureRecord#builder()}.
     * @return {@link ActivityFailureFeedbackRequestBuilder}
     */
    public static ActivityFailureFeedbackRequestBuilder<?, ?> builder() {
        return new ActivityFailureFeedbackRequestBuilderImpl();
    }

    /**
     * OOP-wise, similar to {@link ActivityExecutionFailureRecord#builder(ActivityExecutionRecord)}.
     * @param feedbackRequest {@link ActivityFeedbackRequest}
     * @return {@link ActivityFailureFeedbackRequestBuilder}
     */
    public static ActivityFailureFeedbackRequestBuilder<?, ?> builder(
            ActivityFeedbackRequest feedbackRequest,
            ActivityExecutionStatusEnum newActivityStatus,
            ActivityExecutionFailureRecord.Failure failure
    ) {
        return null == feedbackRequest ? new ActivityFailureFeedbackRequestBuilderImpl()
                : new ActivityFailureFeedbackRequestBuilderImpl()
                .journeyInstanceId(feedbackRequest.getJourneyInstanceId())
                .journeyDefinitionId(feedbackRequest.getJourneyDefinitionId())
                .activityId(feedbackRequest.getActivityId())
                .activityCategory(feedbackRequest.getActivityCategory())
                .executionRecord(
                        ActivityExecutionFailureRecord.builder(
                                feedbackRequest.getExecutionRecord()
                        ).status(newActivityStatus).failure(failure).build()
                );
    }

    // https://www.baeldung.com/lombok-builder-custom-setter#customizing-lombok-builders
    public abstract static class ActivityFailureFeedbackRequestBuilder<
            C extends ActivityFailureFeedbackRequest,
            B extends ActivityFailureFeedbackRequestBuilder<C, B>
            > extends ActivityFeedbackRequestBuilder<C, B> { // https://stackoverflow.com/a/63861259
        public B executionRecord(ActivityExecutionFailureRecord failureRecord) {
            super.executionRecord(failureRecord);
            return this.self();
        }

        /**
         * Please use {@link ActivityFailureFeedbackRequestBuilder#executionRecord(ActivityExecutionFailureRecord)}, instead.
         */
        @Deprecated
        @Override
        public B executionRecord(ActivityExecutionRecord record) {
            if (record instanceof ActivityExecutionFailureRecord) {
                return this.executionRecord((ActivityExecutionFailureRecord) record);
            } else {
                throw new UnsupportedOperationException(String.format("Expected type: %s. Actual type: %s",
                        ActivityExecutionFailureRecord.class.getName(), record.getClass().getName()));
            }
        }
    }
}
