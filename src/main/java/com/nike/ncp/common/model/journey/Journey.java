package com.nike.ncp.common.model.journey;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class Journey {
    @Id
    @ApiModelProperty
    private String id;
    @ApiModelProperty
    private int version;
    @ApiModelProperty
    private String name;
    @ApiModelProperty
    private String description;
    @ApiModelProperty
    private String periodicType;
    @ApiModelProperty
    private Date periodicBegin;
    @ApiModelProperty
    private Date periodicEnd;
    @ApiModelProperty
    private String periodicValues;
    @ApiModelProperty
    private String periodicTimes;
    @ApiModelProperty
    private Date nextStartTime;
    @ApiModelProperty
    private Date lastStartTime;
    @ApiModelProperty
    private long audienceId;
    @ApiModelProperty
    private String audienceName;
    @ApiModelProperty
    private String startActivityId;
    @ApiModelProperty
    private String createdBy;
    @ApiModelProperty
    private String createdByName;
    @ApiModelProperty
    private Date createdTime;
    @ApiModelProperty
    private String updatedBy;
    @ApiModelProperty
    private String updatedByName;
    @ApiModelProperty
    private Date updatedTime;
    @ApiModelProperty
    private String approvedBy;
    @ApiModelProperty
    private String approvedByName;
    @ApiModelProperty
    private Date approvedTime;
    @ApiModelProperty
    private String suspendedBy;
    @ApiModelProperty
    private String suspendedByName;
    @ApiModelProperty
    private Date suspendedTime;
    @ApiModelProperty
    private String status;
    @ApiModelProperty
    private String executionStatus;
    @ApiModelProperty
    private String purpose;
    @ApiModelProperty
    private SendChannel sendChannel;
    @ApiModelProperty
    private boolean freqControl;
    @ApiModelProperty
    private List<String> tags;
    @ApiModelProperty
    private List<JourneyActivity> journeyActivities;
}
