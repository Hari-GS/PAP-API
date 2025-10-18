package com.example.PAP_API.services;

import com.example.PAP_API.dto.ReviewerSummaryDto;
import com.example.PAP_API.enums.Statuses;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportingSummaryService {

    private final AppraisalParticipantRepository participantRepo;

    public ReportingSummaryService(AppraisalParticipantRepository participantRepo) {
        this.participantRepo = participantRepo;
    }

    public List<ReviewerSummaryDto> getReviewerSummary(Long appraisalId) {
        List<AppraisalParticipant> participants = participantRepo.findAllByAppraisalId(appraisalId);

        // Group by reporting person
        Map<NewEmployee, List<AppraisalParticipant>> groupedByReviewer =
                participants.stream()
                        .filter(p -> p.getReportingPerson() != null)
                        .collect(Collectors.groupingBy(AppraisalParticipant::getReportingPerson));

        List<ReviewerSummaryDto> result = new ArrayList<>();

        for (Map.Entry<NewEmployee, List<AppraisalParticipant>> entry : groupedByReviewer.entrySet()) {
            NewEmployee reviewer = entry.getKey();
            List<AppraisalParticipant> assigned = entry.getValue();

            long totalAssigned = assigned.size();
            long completed = assigned.stream()
                    .filter(p -> p.getReviewAppraisalStatus() == Statuses.SUBMITTED)
                    .count();

            boolean allReviewsSubmitted = totalAssigned > 0 && completed == totalAssigned;

            result.add(new ReviewerSummaryDto(
                    reviewer.getEmployeeId(),
                    reviewer.getName(),
                    reviewer.getDesignation(),
                    totalAssigned,
                    completed,
                    allReviewsSubmitted
            ));
        }

        return result;
    }
}
