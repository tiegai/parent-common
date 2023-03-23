package com.nike.ncp.common.model.journey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JourneyActivity {
    private String activityId;
    private String parentActivityId;
    private int activityIndex;
    private String category;
    private String description;
    private AudienceConfig audienceConfig;
    private TraitChoiceConfig traitChoiceConfig;
    private RandomChoiceConfig randomChoiceConfig;
    private SmsConfig smsConfig;
    private InboxPushConfig inboxPushConfig;
    private EmailConfig emailConfig;
    private PromoConfig promoConfig;
    private TagConfig tagConfig;
    private WaitConfig waitConfig;
}
