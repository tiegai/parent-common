package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.journey.ActivityCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(doNotUseGetters = true)
public class ActivityFeedbackEssentials {
    @NonNull
    private ObjectId journeyInstanceId;
    private ObjectId journeyDefinitionId;
    @NonNull
    private ObjectId activityId;
    @NonNull
    private ActivityCategoryEnum activityCategory;
}
