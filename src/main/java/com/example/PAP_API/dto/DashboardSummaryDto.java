package com.example.PAP_API.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummaryDto {
    private int totalEmployees;
    private int completedReviews;
    private int pendingReviews;
    private double averageScore;
}
