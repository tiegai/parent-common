package com.nike.ncp.common.model.approval;

public enum JourneyApprovalRequestEnum {

    REVIEW_STATUS_SUBMITTED("SUBMITTED"),
    REVIEW_STATUS_APPROVED("APPROVED"),
    REVIEW_STATUS_REJECTED("REJECTED"),
    REVIEW_STATUS_CANCELED("CANCELED");

    private final String name;

    JourneyApprovalRequestEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
