package com.nike.ncp.common.model.journey;

public enum WaitConfigEnum {

    UNIT_SECOND("SECOND"),
    UNIT_MINUTE("MINUTE"),
    UNIT_HOUR("HOUR"),
    UNIT_DAY("DAY");

    private String name;

    WaitConfigEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
