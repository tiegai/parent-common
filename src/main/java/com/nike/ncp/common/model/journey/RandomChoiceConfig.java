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
public class RandomChoiceConfig {
    public static final String RANDOM_TYPE_WEIGHT = "WEIGHT";
    public static final String RANDOM_TYPE_NUMBER = "NUMBER";

    private String randomType;
    private List<RandomBranch> randomBranches;
}
