package com.nike.ncp.common.model.freqctrl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreqCtrlCrossChannel {
    private List<String> channelTypes;
    private String cycle;
    private String frequency;
}
