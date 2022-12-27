package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.ActivityExecutionStatusEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@SuperBuilder
public class ActivityStatusQueryResponse extends BaseJourneyActivity {
    private List<Activity> activities;

    @Data
    @SuperBuilder
    public static class Activity extends BaseJourneyActivity.Activity {
        private ObjectId id;
        private ActivityExecutionStatusEnum status;
        private Throwable error;
    }
}
