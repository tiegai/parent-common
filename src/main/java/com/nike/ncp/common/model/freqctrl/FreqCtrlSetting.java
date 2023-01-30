package com.nike.ncp.common.model.freqctrl;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "freq_ctrl_setting")
public class FreqCtrlSetting {
    @Id
    private String id;
    private String controlId;
    private String controlLevel;
    private List<FreqCtrlSingleChannel> freqCtrlSingleChannel;
    private List<FreqCtrlCrossChannel> freqCtrlCrossChannel;
    private String createdTime;
    private String updatedTime;
}
