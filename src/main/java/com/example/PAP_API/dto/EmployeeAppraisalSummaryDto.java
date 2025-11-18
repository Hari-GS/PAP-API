package com.example.PAP_API.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeAppraisalSummaryDto {
    private Long appraisalId;

    private String title;
    private String status;
    private String type;

    private String startDate;
    private String selfAppraisalEndDate;
    private String endDate;
    private String stage;

    private String reportingManagerName;
    private String reportingManagerDesignation;

    private String createdAt;
    private String createdBy;

    private Integer selfQnsAnswered;
    private Long totalSelfQns;

    private Integer totalManagerReviews;
    private Integer totalManagerReviewsDone;
}