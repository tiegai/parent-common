package com.nike.ncp.common.executor.task;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nike.ncp.common.executor.properties.EcsMetadata;
import com.nike.ncp.common.model.journey.ActivityCategoryEnum;
import com.nike.ncp.common.model.proxy.ActivityExecutionFailureRecord;
import com.nike.ncp.common.model.proxy.ActivityExecutionStatusEnum;
import com.nike.ncp.common.model.proxy.ActivityFeedbackRequest;
import com.nike.ncp.common.model.proxy.DispatchedActivity;
import com.nike.ncp.common.model.util.ObjectIdSerializer;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * 1. Create a concrete class with a constructor matching super and override super abstract methods:
 * <pre class="code">
 * public class ConcreteTask extends {@link ActivityDispatchTask}&#60;T&#62; {
 *      // It is intentionally mandatory to create a constructor matching super.
 *      // Because <a href="https://stackoverflow.com/a/29771875">Lombok is not capable of create such a constructor</a> for you.
 *      public ConcreteTask(
 *          ObjectId journeyInstanceId,
 *          ObjectId activityId,
 *          {@link DispatchedActivity} dispatchedActivity
 *      ) {
 *          super(journeyInstanceId, activityId, dispatchedActivity);
 *      }
 *
 *      &#64;Override
 *      protected T main() {
 *          // all your main logic
 *      }
 *
 *      &#64;Override
 *      protected boolean isSuccessful(String result) {
 *          // predicate the result of your {@link ActivityDispatchTask#main} to be a success or failure
 *      }
 * }
 * </pre>
 * 2. Instantiate your {@code ConcreteTask} and then
 * {@link ActivityDispatchTask#execAsync()} to process your task asynchronously.
 * <pre class="code">
 * ConcreteTask task = new ConcreteTask(journeyInstanceId, activityId, activityPayload);
 * try {
 *     Future&#60;T&#62; result = task.execAsync();
 * } catch (Exception e) {
 *     // handle your exception
 * }
 * </pre>
 * @param <T> the return data type of your {@link ActivityDispatchTask#main}
 */
@Deprecated
@Data
@RequiredArgsConstructor
public abstract class ActivityDispatchTask<T> {
    @NonNull
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId journeyInstanceId;
    @NonNull
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId activityId;
    @NonNull
    private DispatchedActivity dispatchedActivity;

    /**
     * Runs everything asynchronously.
     * @return {@link Future}&#60;T&#62
     * @throws Exception
     */
    public Future<T> execAsync() throws Exception {
        // TODO acquire scale-in protection
        T result = null;
        try {
            result = this.main();
        } catch (Exception e) {
            this.handleMainException(e);
        }
        this.feedBack(result);
        // TODO release scale-in protection
        return null; // TODO return a real Future
    }

    /**
     * Your main logic goes here.
     * @return T
     */
    protected abstract T main();

    /**
     * Whether the outcome of {@link #main} should be considered successful or failed.
     * @param result the outcome of {@link #main}
     * @return successful or failed
     */
    protected abstract boolean isSuccessful(T result);

    protected void feedBack(T result) {
        if (isSuccessful(result)) {
            // TODO feed back as success
        } else {
            // TODO feed back as failure
        }
    }

    protected <E extends Exception> void handleMainException(E exception) throws E {
        final ObjectId journeyDefinitionId = Objects.requireNonNullElse(
                dispatchedActivity.getJourney(), new DispatchedActivity.Journey()
        ).getDefinitionId();
        final @NonNull ActivityCategoryEnum activityCategory = Objects.requireNonNullElse(
                dispatchedActivity.getActivity(), new DispatchedActivity.Activity<>()
        ).getCategory();
        var executionRecord = ActivityExecutionFailureRecord.builder()
                .ecsTaskArn(EcsMetadata.getLabels().get("com.amazonaws.ecs.task-arn"))
                .privateIp(EcsMetadata.getNetworks().get(0).getIPv4Addresses().get(0))
                .status(ActivityExecutionStatusEnum.FAILED)
                .failure(ActivityExecutionFailureRecord.Failure.builder().message(exception.getMessage()).build())
                .endTime(LocalDateTime.now())
                .build();

        var failure = ActivityFeedbackRequest.builder()
                .journeyDefinitionId(journeyDefinitionId)
                .journeyInstanceId(this.journeyInstanceId)
                .activityId(this.activityId)
                .activityCategory(activityCategory)
                .executionRecord(executionRecord)
                .build();

        assert null != failure;

        // TODO send failure to ncp-proxy

        throw exception;
    }
}
