package com.nike.ncp.common.model.journey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraitBranch {
    public static final String TRAIT_JOINER_AND = "AND";
    public static final String TRAIT_JOINER_OR = "OR";
    public static final String TRAIT_JOINER_NOT = "NOT";

    private boolean otherBranch;
    private String logicalJoiner;
    private List<TraitCondition> traitConditions;
}
