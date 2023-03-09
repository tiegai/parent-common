package com.nike.ncp.common.utilities.model;

public enum OperTypeEnum {

    CREATE("CREATE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    OTHER("OTHER");


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
