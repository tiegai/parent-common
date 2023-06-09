package com.nike.ncp.common.model.journey;

public enum TraitBranchEnum {

    TRAIT_JOINER_AND("AND"),
    TRAIT_JOINER_OR("OR");

    private String name;

    TraitBranchEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
