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
    public static final String CATEGORY_START = "START";
    public static final String CATEGORY_END = "END";
    public static final String CATEGORY_AUDIENCE = "AUDIENCE";
    public static final String CATEGORY_TRAIT_CHOICE = "TRAIT_CHOICE";
    public static final String CATEGORY_RANDOM_CHOICE = "RANDOM_CHOICE";
    public static final String CATEGORY_SMS = "SMS";
    public static final String CATEGORY_PUSH = "PUSH";
    public static final String CATEGORY_EMAIL = "EMAIL";
    public static final String CATEGORY_INBOX = "INBOX";
    public static final String CATEGORY_TAG = "TAG";
    public static final String CATEGORY_WAIT = "WAIT";

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
