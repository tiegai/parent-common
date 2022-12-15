package com.nike.ncp.common.model.journey;

public enum JourneyEnum {

    PERIODIC_ONCE("ONCE"),
    PERIODIC_DAILY("DAILY"),
    PERIODIC_WEEKLY("WEEKLY"),
    PERIODIC_MONTHLY("MONTHLY"),

    STATUS_NEW("NEW"),
    STATUS_APPROVED("APPROVED"),
    STATUS_SUSPENDED("SUSPENDED"),

    ID("id"),
    VERSION("version"),
    NAME("name"),
    DESCRIPTION("description"),
    PROGRAM_ID("programId"),
    CAMPAIGN_ID("campaignId"),
    SUB_CAMPAIGN_ID("subCampaignId"),
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
    JOURNEY_ACTIVITIES("journeyActivities");

    private String name;

    JourneyEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
