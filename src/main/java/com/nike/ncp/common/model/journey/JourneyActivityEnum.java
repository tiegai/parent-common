package com.nike.ncp.common.model.journey;

public enum JourneyActivityEnum {

    CATEGORY_START("START"),
    CATEGORY_END("END"),
    CATEGORY_AUDIENCE("AUDIENCE"),
    CATEGORY_TRAIT_CHOICE("TRAIT_CHOICE"),
    CATEGORY_RANDOM_CHOICE("RANDOM_CHOICE"),
    CATEGORY_SMS("SMS"),
    CATEGORY_PUSH("PUSH"),
    CATEGORY_EMAIL("EMAIL"),
    CATEGORY_INBOX("INBOX"),
    CATEGORY_TAG("TAG"),
    CATEGORY_WAIT("WAIT");

    private String name;

    JourneyActivityEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
