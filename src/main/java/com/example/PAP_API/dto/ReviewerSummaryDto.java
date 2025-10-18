package com.example.PAP_API.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewerSummaryDto {
    private String reviewerId;
    private String reviewerName;
    private String designation;
    private Long totalAssigned;
    private Long completed;
    private boolean allReviewsSubmitted;
}
