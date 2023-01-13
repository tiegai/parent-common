package com.nike.ncp.common.model.proxy;

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
@ToString(doNotUseGetters = true)
public class ActivityFeedbackRequest extends ActivityFeedbackEssentials {
    /**
     * If you're feeding back failure of an {@link DispatchedActivity},
     * please set this field to a {@link ActivityExecutionFailureRecord}
     * with a non-null {@link ActivityExecutionFailureRecord#failure}.
     */
    @NonNull
    private ActivityExecutionRecord executionRecord;
}
