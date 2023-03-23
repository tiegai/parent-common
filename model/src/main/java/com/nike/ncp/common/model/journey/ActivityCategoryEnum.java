package com.nike.ncp.common.model.journey;

public enum ActivityCategoryEnum {

    START("START"),
    END("END"),
    AUDIENCE("AUDIENCE"),
    TRAIT_CHOICE("TRAIT_CHOICE"),
    RANDOM_CHOICE("RANDOM_CHOICE"),
    FREQ_CTRL("FREQ_CTRL"),
    SMS("SMS"),
    INBOX_PUSH("INBOX_PUSH"),
    EMAIL("EMAIL"),
    PROMO("PROMO"),
    TAG("TAG"),
    WAIT("WAIT");

    private final String name;

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
