package com.nike.ncp.common.model.journey;

public enum TagConfigEnum {

    SOURCE_NAC("NAC"),
    SOURCE_NCP("NCP"),

    TYPE_STRING("STRING"),
    TYPE_NUMBER("NUMBER"),
    TYPE_BOOL("BOOL"),

    SYMBOL_EXIST("EXIST"),
    SYMBOL_NOT_EXIST("NOT_EXIST"),

    SYMBOL_EQUAL("EQUAL"),
    SYMBOL_NOT_EQUAL("NOT_EQUAL"),
    SYMBOL_GREATER("GREATER"),
    SYMBOL_GREATER_EQUAL("GREATER_EQUAL"),
    SYMBOL_LESS("LESS"),
    SYMBOL_LESS_EQUAL("LESS_EQUAL");

    private String name;

    TagConfigEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
