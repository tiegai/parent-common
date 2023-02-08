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

/**
 * This is how a {@link DispatchedActivity} circulates:
 * <br/>
 * 1. <a href="https://github.com/nike-gc-ncp/ncp-journeyengine">ncp-journeyengine</a> creates a {@link DispatchedActivity}
 * and calls <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a>'s controller to dispatch.
 * <br/>
 * 2. <a href="https://github.com/nike-gc-ncp/ncp-proxy">ncp-proxy</a> receives
 * and passes this {@link DispatchedActivity} to an OneNCP executor that matches its {@link Activity#category}.
 * @param <ACTIVITY_CONFIG> data type of {@link Activity#config}
 *                         specific to the {@link Activity#category} of {@link DispatchedActivity#activity}
 */
@Data
@SuperBuilder
@NoArgsConstructor
@RequiredArgsConstructor
public class DispatchedActivity<ACTIVITY_CONFIG> {
    @NonNull
    private Journey journey;
    @NonNull
    private Activity<ACTIVITY_CONFIG> activity;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Journey {
        @NonNull
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId instanceId;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId definitionId;
        private Integer version;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId programId;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId campaignId;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId subCampaignId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Activity<ACTIVITY_CONFIG> {
        @NonNull
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId id;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId parentActivityId;
        @JsonSerialize(using = ObjectIdSerializer.class)
        private ObjectId nextActivityId;
        private Integer activityIndex;
        @NonNull
        private ActivityCategoryEnum category;
        /**
         * You are responsible for specifying the {@link ACTIVITY_CONFIG} type according to {@link Activity#category}.<br/>
         * Consult authors of <a href="https://github.com/nike-gc-ncp/ncp-journeyengine">ncp-journeyengine</a>
         * for a list of available {@code ACTIVITY_CONFIG} types.
         */
        private ACTIVITY_CONFIG config;
    }
}
