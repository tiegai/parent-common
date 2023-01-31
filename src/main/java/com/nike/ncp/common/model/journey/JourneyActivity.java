package com.nike.ncp.common.model.journey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JourneyActivity {
    @Id
    private String id;
    private String parentActivityId;
    private int activityIndex;
    private String category;
    private String description;
    private AudienceConfig audienceConfig;
    private TraitChoiceConfig traitChoiceConfig;
    private RandomChoiceConfig randomChoiceConfig;
    private SmsConfig smsConfig;
    private PushConfig pushConfig;
    private EmailConfig emailConfig;
    private InboxConfig inboxConfig;
    private TagConfig tagConfig;
    private WaitConfig waitConfig;
}
