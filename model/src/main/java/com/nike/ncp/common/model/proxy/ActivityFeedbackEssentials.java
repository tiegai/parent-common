package com.nike.ncp.common.model.proxy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nike.ncp.common.model.journey.ActivityCategoryEnum;
import com.nike.ncp.common.model.util.ObjectIdSerializer;
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
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId journeyInstanceId;
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId journeyDefinitionId;
    @NonNull
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId activityId;
    @NonNull
    private ActivityCategoryEnum activityCategory;
}
