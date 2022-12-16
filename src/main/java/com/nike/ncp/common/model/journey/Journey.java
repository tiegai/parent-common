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
    @Id
    private String id;
    private int version;
    private String name;
    private String description;
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
    private boolean freqControl;
    private List<JourneyActivity> journeyActivities;
}
