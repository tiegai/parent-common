package com.nike.ncp.common.model.freqctrl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreqCtrlSingleChannel {
    private String channelType;
    private String cycle;
    private String frequency;
}
