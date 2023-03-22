package com.nike.ncp.common.utilities.model;

public enum OperTypeEnum {

    CREATE("CREATE"),
    EDIT("EDIT"),
    DELETE("DELETE"),
    SUBMIT("SUBMIT"),
    CANCEL("CANCEL"),
    APPROVE("APPROVE"),
    REJECT("REJECT"),
    RESUME("RESUME"),
    SUSPEND("SUSPEND"),
    LOGIN("LOGIN");


    private String name;

    OperTypeEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
