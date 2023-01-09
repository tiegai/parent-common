package com.nike.ncp.common.model.journey;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ApiModel
public class SendChannel {
    @ApiModelProperty
    private boolean sms;
    @ApiModelProperty
    private boolean push;
    @ApiModelProperty
    private boolean email;
    @ApiModelProperty
    private boolean inbox;
}
