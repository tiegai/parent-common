package com.nike.ncp.common.model.journey;

public enum RandomChoiceConfigEnum {

    RANDOM_TYPE_WEIGHT("WEIGHT"),
    RANDOM_TYPE_NUMBER("NUMBER");

    private String name;

    RandomChoiceConfigEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
