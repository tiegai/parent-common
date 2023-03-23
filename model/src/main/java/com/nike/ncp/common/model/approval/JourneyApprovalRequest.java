package com.nike.ncp.common.model.approval;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "journey_approval_request")
public class JourneyApprovalRequest {
    @Id
    private String id;
    private String journeyId;
    private String journeyName;
    private String journeyNameLowerCase;
    private String reviewStatus;
    private String reviewComment;
    private String reviewedBy;
    private String reviewedByName;
    private Date reviewedTime;
    private String createdBy;
    private String createdByName;
    private Date createdTime;
    private String updatedBy;
    private String updatedByName;
    private Date updatedTime;
}
