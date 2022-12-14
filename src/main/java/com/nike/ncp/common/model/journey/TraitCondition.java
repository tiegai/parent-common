package com.nike.ncp.common.model.journey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraitCondition {
    public static final String JOINER_AND = "AND";
    public static final String JOINER_OR = "OR";
    public static final String JOINER_NOT = "NOT";

    public static final String SOURCE_NAC = "NAC";
    public static final String SOURCE_NCP = "NCP";

    public static final String TYPE_STRING = "STRING";
    public static final String TYPE_NUMBER = "NUMBER";
    public static final String TYPE_BOOL = "BOOL";

    public static final String SYMBOL_EXIST = "EXIST";
    public static final String SYMBOL_NOT_EXIST = "NOT_EXIST";

    public static final String SYMBOL_EQUAL = "EQUAL";
    public static final String SYMBOL_GREATER = "GREATER";
    public static final String SYMBOL_GREATER_EQUAL = "GREATER_EQUAL";
    public static final String SYMBOL_LESS = "LESS";
    public static final String SYMBOL_LESS_EQUAL = "LESS_EQUAL";

    private String logicalJoiner;
    private String traitSource;
    private String traitType;
    private String traitId;
    private String traitSymbol;
    private String traitValue;
}
