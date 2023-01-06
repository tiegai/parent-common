package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.journey.ActivityCategoryEnum;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class ActivityFeedbackRequest {
    @NonNull
    private ObjectId journeyInstanceId;
    private ObjectId journeyDefinitionId;
    @NonNull
    private ObjectId activityId;
    private ActivityCategoryEnum activityCategory;
    /**
     * If you're feeding back failure of an {@link DispatchedActivity},
     * make sure you will set an non-null {@code error} field in the {@link ActivityExecutionRecord}.
     */
    @NonNull
    private ActivityExecutionRecord executionRecord;
}
