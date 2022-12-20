package com.nike.ncp.common.model.journey;

public enum TraitConditionEnum {

    JOINER_AND("AND"),
    JOINER_OR("OR"),

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

    TraitConditionEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
