package com.nike.ncp.common.utilities.model;

public enum ResourceTypeEnum {

    JOURNEY("Journey"),
    USER("User"),
    SMS_TEMPLATE("SMS"),
    EMAIL_TEMPLATE("Email"),
    PUSH_TEMPLATE("Push"),
    ROLE("Role");


    private String name;

    ResourceTypeEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
