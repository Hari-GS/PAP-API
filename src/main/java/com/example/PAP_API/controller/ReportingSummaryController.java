package com.example.PAP_API.controller;

import com.example.PAP_API.dto.ReviewerSummaryDto;
import com.example.PAP_API.services.ReportingSummaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reporting")
public class ReportingSummaryController {

    private final ReportingSummaryService summaryService;

    public ReportingSummaryController(ReportingSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/reviewers-summary/{appraisalId}")
    public List<ReviewerSummaryDto> getReviewerSummary(@PathVariable Long appraisalId) {
        return summaryService.getReviewerSummary(appraisalId);
    }
}
