package com.nike.ncp.common.model.proxy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Sample {@link ActivityFeedbackRequest} in JSON representation.
 * <pre class="code">
 * {
 *     "journeyInstanceId": "63998f7c958e0e77f20ed8ee",
 *     "activityId": "63998f7c958e0e77f20ed8ee",
 *     "activityCategory": "FREQ_CTRL",
 *     "executionRecord": {
 *         "privateIp": "127.0.0.2",
 *         "ecsTaskArn": "arn:aws-cn:ecs:cn-northwest-1:AWS_ACCOUNT_NO:task/onencp-test-cluster/1b73182c9229458b81044c9f9ef60645",
 *         "status": "DONE"
 *     }
 * }
 * </pre>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class ActivityFeedbackRequest extends ActivityFeedbackEssentials {
    /**
     * If you're feeding back failure of an {@link DispatchedActivity},
     * please set this field to a {@link ActivityExecutionFailureRecord}
     * with a non-null {@link ActivityExecutionFailureRecord#failure}.
     */
    @NonNull
    private ActivityExecutionRecord executionRecord;
}
