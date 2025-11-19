package com.example.PAP_API.services;

import com.example.PAP_API.dto.DashboardSummaryDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.enums.Statuses;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import com.example.PAP_API.repository.AppraisalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private AppraisalParticipantRepository participantRepo;

    @Autowired
    private AppraisalRepository appraisalRepo;

    @Autowired
    private UserService userService;

    public DashboardSummaryDto getDashboardSummary() {
        Long hrId = userService.resolveHrIdForUser();
        // Get the most recent appraisal for this HR
        Appraisal recentAppraisal = appraisalRepo.findTopByHrManagerIdOrderByIdDesc(hrId)
                .orElseThrow(() -> new EntityNotFoundException("No recent appraisals found"));

        Long appraisalId = recentAppraisal.getId();

        long totalEmployees = participantRepo.countByAppraisalId(appraisalId);
        long completedSelf = participantRepo.countSelfReviewsCompleted(appraisalId, Statuses.SUBMITTED);
        long completedMgr = participantRepo.countManagerReviewsCompleted(appraisalId, Statuses.SUBMITTED);
        long totalReportingReviewsToDo = participantRepo.countParticipantsWithReportingPerson(appraisalId);

        return new DashboardSummaryDto(
                totalEmployees,
                completedSelf,
                completedMgr,
                totalReportingReviewsToDo
        );
    }
}
