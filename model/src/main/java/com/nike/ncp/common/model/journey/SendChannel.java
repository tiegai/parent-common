package com.nike.ncp.common.model.journey;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class SendChannel {
    private boolean sms;
    private boolean push;
    private boolean email;
    private boolean inbox;
}
