package com.example.PAP_API.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummaryDto {
    private long totalEmployees;
    private long selfReviewsCompleted;
    private long reportingReviewsCompleted;
    private long totalReportingReviewsToDo;
}
