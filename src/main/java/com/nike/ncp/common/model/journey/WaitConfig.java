package com.nike.ncp.common.model.journey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaitConfig {
    public static final String UNIT_SECOND = "SECOND";
    public static final String UNIT_MINUTE = "MINUTE";
    public static final String UNIT_DAY = "DAY";
    public static final String UNIT_WEEK = "WEEK";

    private String unit;
    private int value;
}
