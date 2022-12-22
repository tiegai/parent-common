package com.nike.ncp.common.model.journey;

public enum ActivityCategoryEnum {

    START("START"),
    END("END"),
    AUDIENCE("AUDIENCE"),
    TRAIT_CHOICE("TRAIT_CHOICE"),
    RANDOM_CHOICE("RANDOM_CHOICE"),
    SMS("SMS"),
    PUSH("PUSH"),
    EMAIL("EMAIL"),
    INBOX("INBOX"),
    TAG("TAG"),
    WAIT("WAIT");

    private String name;

    ActivityCategoryEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
