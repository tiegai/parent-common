package com.nike.ncp.common.model.proxy;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Deprecated. Please use {@link ActivityFeedbackRequest}, instead.
 */
@Deprecated
@Data
@SuperBuilder
@RequiredArgsConstructor
public class ActivityFailureFeedbackRequest extends BaseJourneyActivity {
    @NonNull
    private ActivityExecutionRecord execution;
    @NonNull
    private Throwable error;
}
