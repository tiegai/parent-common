package com.nike.ncp.common.model.proxy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nike.ncp.common.model.journey.ActivityCategoryEnum;
import com.nike.ncp.common.model.util.ObjectIdSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Deprecated
@Data
@SuperBuilder
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class BaseJourneyActivity {
    @NonNull
    private Journey journey;
    @NonNull
    private Activity activity;

    @Data
    @SuperBuilder
    @RequiredArgsConstructor
    public static class Journey {
        @JsonSerialize(using = ObjectIdSerializer.class)
        private final ObjectId definitionId;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private final ObjectId instanceId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Activity {
        @NonNull
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId id;
        @NonNull
        private ActivityCategoryEnum category;
    }
}
