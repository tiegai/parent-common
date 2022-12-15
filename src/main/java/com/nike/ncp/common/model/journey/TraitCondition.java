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
    private String logicalJoiner;
    private String traitSource;
    private String traitType;
    private String traitId;
    private String traitSymbol;
    private String traitValue;
}
