package com.nike.ncp.common.model.journey;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "journey")
public class Journey {
    public static final String PERIODIC_ONCE = "ONCE";
    public static final String PERIODIC_DAILY = "DAILY";
    public static final String PERIODIC_WEEKLY = "WEEKLY";
    public static final String PERIODIC_MONTHLY = "MONTHLY";

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_SUSPENDED = "SUSPENDED";

    public static final String ID = "id";
    public static final String VERSION = "version";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PROGRAM_ID = "programId";
    public static final String CAMPAIGN_ID = "campaignId";
    public static final String SUB_CAMPAIGN_ID = "subCampaignId";
    public static final String PERIODIC_TYPE = "periodicType";
    public static final String PERIODIC_BEGIN = "periodicBegin";
    public static final String PERIODIC_END = "periodicEnd";
    public static final String PERIODIC_VALUES = "periodicValues";
    public static final String PERIODIC_TIMES = "periodicTimes";
    public static final String NEXT_START_TIME = "nextStartTime";
    public static final String LAST_START_TIME = "lastStartTime";
    public static final String AUDIENCE_ID = "audienceId";
    public static final String AUDIENCE_NAME = "audienceName";
    public static final String START_ACTIVITY_ID = "startActivityId";
    public static final String CREATED_BY = "createdBy";
    public static final String CREATED_BY_NAME = "createdByName";
    public static final String CREATED_TIME = "createdTime";
    public static final String UPDATED_BY = "updatedBy";
    public static final String UPDATED_BY_NAME = "updatedByName";
    public static final String UPDATED_TIME = "updatedTime";
    public static final String APPROVED_BY = "approvedBy";
    public static final String APPROVED_BY_NAME = "approvedByName";
    public static final String APPROVED_TIME = "approvedTime";
    public static final String SUSPENDED_BY = "suspendedBy";
    public static final String SUSPENDED_BY_NAME = "suspendedByName";
    public static final String SUSPENDED_TIME = "suspendedTime";
    public static final String STATUS = "status";
    public static final String JOURNEY_ACTIVITIES = "journeyActivities";

    @Id
    private String id;
    private int version;
    private String name;
    private String description;
    private String programId;
    private String campaignId;
    private String subCampaignId;
    private String periodicType;
    private Date periodicBegin;
    private Date periodicEnd;
    private String periodicValues;
    private String periodicTimes;
    private Date nextStartTime;
    private Date lastStartTime;
    private long audienceId;
    private String audienceName;
    private String startActivityId;
    private String createdBy;
    private String createdByName;
    private Date createdTime;
    private String updatedBy;
    private String updatedByName;
    private Date updatedTime;
    private String approvedBy;
    private String approvedByName;
    private Date approvedTime;
    private String suspendedBy;
    private String suspendedByName;
    private Date suspendedTime;
    private String status;
    private List<JourneyActivity> journeyActivities;
}
