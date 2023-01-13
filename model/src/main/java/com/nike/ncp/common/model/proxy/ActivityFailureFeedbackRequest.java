package com.nike.ncp.common.model.proxy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(doNotUseGetters = true)
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
        this.setExecutionRecord((ActivityExecutionFailureRecord) executionRecord);
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
    public static ActivityFailureFeedbackRequestBuilder<?, ?> builder(ActivityFeedbackRequest feedbackRequest) {
        return null == feedbackRequest ? new ActivityFailureFeedbackRequestBuilderImpl()
                : new ActivityFailureFeedbackRequestBuilderImpl()
                .journeyInstanceId(feedbackRequest.getJourneyInstanceId())
                .journeyDefinitionId(feedbackRequest.getJourneyDefinitionId())
                .activityId(feedbackRequest.getActivityId())
                .activityCategory(feedbackRequest.getActivityCategory())
                /* You most likely want to replace this {@link ActivityFeedbackRequest#executionRecord}
                    with a {@link ActivityExecutionFailureRecord} afterwards. */
                .executionRecord(feedbackRequest.getExecutionRecord());
    }

    // https://www.baeldung.com/lombok-builder-custom-setter#customizing-lombok-builders
    public abstract static class ActivityFailureFeedbackRequestBuilder<
            C extends ActivityFailureFeedbackRequest,
            B extends ActivityFailureFeedbackRequestBuilder<C, B>
            > extends ActivityFeedbackRequestBuilder<C, B> { // https://stackoverflow.com/a/63861259
        private ActivityExecutionFailureRecord executionRecord;

        public B executionRecord(ActivityExecutionFailureRecord executionRecord) {
            this.executionRecord = executionRecord;
            return this.self();
        }

        /**
         * Please use {@link ActivityFailureFeedbackRequestBuilder#executionRecord(ActivityExecutionFailureRecord)}, instead.
         */
        @Deprecated
        @Override
        public B executionRecord(ActivityExecutionRecord executionRecord) {
            return this.executionRecord((ActivityExecutionFailureRecord) executionRecord);
        }
    }
}
