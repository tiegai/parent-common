package com.nike.ncp.common.model.journey;

public enum JourneyEnum {

    SCHEDULE_TYPE_ONCE("ONCE"),
    SCHEDULE_TYPE_PERIODIC("PERIODIC"),

    PERIODIC_ONCE("ONCE"),
    PERIODIC_DAILY("DAILY"),
    PERIODIC_WEEKLY("WEEKLY"),
    PERIODIC_MONTHLY("MONTHLY"),

    STATUS_DRAFT("DRAFT"),
    STATUS_SUBMITTED("SUBMITTED"),
    STATUS_REJECTED("REJECTED"),
    STATUS_APPROVED("APPROVED"),

    EXECUTION_STATUS_NOT_READY("NOT_READY"),
    EXECUTION_STATUS_READY("READY"),
    EXECUTION_STATUS_COMPLETED("COMPLETED"),
    EXECUTION_STATUS_RUNNING("RUNNING"),
    EXECUTION_STATUS_SUSPENDED("SUSPENDED"),
    EXECUTION_STATUS_FAILED("FAILED"),

    PURPOSE_ADHOC("ADHOC"),

    ID("id"),
    VERSION("version"),
    NAME("name"),
    DESCRIPTION("description"),
    SCHEDULE_TYPE("scheduleType"),
    PERIODIC_TYPE("periodicType"),
    PERIODIC_BEGIN("periodicBegin"),
    PERIODIC_END("periodicEnd"),
    PERIODIC_VALUES("periodicValues"),
    PERIODIC_TIMES("periodicTimes"),
    NEXT_START_TIME("nextStartTime"),
    LAST_START_TIME("lastStartTime"),
    AUDIENCE_ID("audienceId"),
    AUDIENCE_NAME("audienceName"),
    START_ACTIVITY_ID("startActivityId"),
    CREATED_BY("createdBy"),
    CREATED_BY_NAME("createdByName"),
    CREATED_TIME("createdTime"),
    UPDATED_BY("updatedBy"),
    UPDATED_BY_NAME("updatedByName"),
    UPDATED_TIME("updatedTime"),
    APPROVED_BY("approvedBy"),
    APPROVED_BY_NAME("approvedByName"),
    APPROVED_TIME("approvedTime"),
    SUSPENDED_BY("suspendedBy"),
    SUSPENDED_BY_NAME("suspendedByName"),
    SUSPENDED_TIME("suspendedTime"),
    STATUS("status"),
    EXECUTION_STATUS("executionStatus"),
    PURPOSE("purpose"),
    TAGS("tags"),
    JOURNEY_ACTIVITIES("journeyActivities");

    private String name;

    JourneyEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
