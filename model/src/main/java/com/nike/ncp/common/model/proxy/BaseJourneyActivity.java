package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.journey.ActivityCategoryEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

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
        private final ObjectId definitionId;
        private final ObjectId instanceId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Activity {
        private ActivityCategoryEnum category;
    }
}
