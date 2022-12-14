package com.nike.ncp.common.model.journey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsConfig {
    public static final String SMS_VENDOR_ZHUJI = "ZHUJI";

    private String vendor;
    private String templateId;
    private String cpCode;
}
