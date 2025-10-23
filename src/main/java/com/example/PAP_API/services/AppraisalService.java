package com.example.PAP_API.services;

import com.example.PAP_API.dto.AppraisalDto;
import com.example.PAP_API.enums.Stage;
import com.example.PAP_API.exception.ResourceNotFoundException;
import com.example.PAP_API.mappers.AppraisalMapper;
import com.example.PAP_API.model.*;
import com.example.PAP_API.repository.AppraisalRepository;
import com.example.PAP_API.repository.HRManagerRepository;
import com.example.PAP_API.repository.NewEmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AppraisalService {

    @Autowired
    private AppraisalRepository appraisalRepository;

    @Autowired
    private AppraisalMapper appraisalMapper;

    @Autowired
    HRManagerRepository hrManagerRepository;

    @Autowired
    NewEmployeeRepository newEmployeeRepository;

    @Autowired
    UserContextService userContextService;

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    public Appraisal saveAppraisal(AppraisalDto dto, Long hrId) {
        Appraisal appraisal = appraisalMapper.toEntity(dto);

        // Set HR manager for this appraisal
        appraisal.setHrManager(hrManagerRepository.findById(hrId).orElseThrow(() -> new RuntimeException("HRManager not found")));

        // Set back-references for participants and questions
        if (appraisal.getParticipants() != null) {
            for (AppraisalParticipant participant : appraisal.getParticipants()) {
                participant.setAppraisal(appraisal);

                // Set the actual participant employee (fetch from DB)
                NewEmployee emp = newEmployeeRepository
                        .findByEmployeeIdAndHrManagerId(participant.getEmployeeId(), hrId)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                participant.setParticipant(emp);

                // Set the reporting person (fetch from DB too)
                if (participant.getReportingPerson() != null) {
                    NewEmployee manager = newEmployeeRepository
                            .findByEmployeeIdAndHrManagerId(
                                    participant.getReportingPerson().getEmployeeId(),
                                    hrId
                            )
                            .orElseThrow(() -> new RuntimeException("Manager not found"));
                    participant.setReportingPerson(manager);
                }

                // Handle questions
                if (participant.getQuestions() != null) {
                    for (AppraisalQuestion question : participant.getQuestions()) {
                        question.setId(null);
                        question.setParticipant(participant);
                    }
                }

                participant.setTotalQns((long) participant.getQuestions().size());
            }
        }
        Appraisal savedAppraisal = appraisalRepository.save(appraisal);

        // --- Send email notifications ---
        sendParticipantEmails(savedAppraisal);

        return savedAppraisal;
    }

    private void sendParticipantEmails(Appraisal appraisal) {
        if (appraisal.getParticipants() == null || appraisal.getParticipants().isEmpty()) return;

        for (AppraisalParticipant participant : appraisal.getParticipants()) {
            try {
                String email = participant.getParticipant().getEmail();
                String name = participant.getParticipant().getName();
                String appraisalName = appraisal.getTitle();
                String appraisalType = appraisal.getType();
                String startDate = appraisal.getStartDate().toString();
                String reportingReviewStartDate = appraisal.getSelfAppraisalEndDate().toString();
                String endDate = appraisal.getEndDate().toString();
                String hrManagerName = appraisal.getHrManager().getName();

                String body = emailTemplateService.getAppraisalCreatedForParticipantEmail(name, appraisalName, appraisalType, startDate, reportingReviewStartDate, endDate, hrManagerName);
                emailService.sendPlainTextMail(email,
                        "New Performance Appraisal Cycle Launched - " + appraisalName + " " + appraisalType,
                        body);
            } catch (Exception e) {
                System.err.println("Error sending email to participant: " + e.getMessage());
            }
        }
    }


    public List<Appraisal> getAllAppraisals(Long hrId) {
        return appraisalRepository.findByHrManagerIdAndStageNot(hrId,Stage.CLOSED);
    }

    public AppraisalDto getAppraisalById(Long id) {
        Appraisal appraisal =  appraisalRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Appraisal not found"));
        AppraisalDto dto =  appraisalMapper.toDto(appraisal);
        dto.setCreatedBy(appraisal.getHrManager().getName());
        return dto;
    }

    public Appraisal moveToNextStage(Long appraisalId, Long hrId) {
        Appraisal appraisal = appraisalRepository.findByIdAndHrManagerId(appraisalId, hrId)
                .orElseThrow(() -> new RuntimeException("Appraisal not found for this HR"));

        Stage currentStage = appraisal.getStage();
        Stage nextStage = getNextStage(currentStage);

        if (nextStage == null) {
            throw new RuntimeException("Appraisal is already in the final stage.");
        }

        appraisal.setStage(nextStage);
        Appraisal savedAppraisal = appraisalRepository.save(appraisal);

        // --- Notify participants based on the new stage ---
        notifyParticipantsByStage(savedAppraisal, nextStage);

        return savedAppraisal;
    }

    private void notifyParticipantsByStage(Appraisal appraisal, Stage nextStage) {
        List<AppraisalParticipant> participants = appraisal.getParticipants();
        if (participants == null || participants.isEmpty()) return;

        String appraisalTitle = appraisal.getTitle();
        String appraisalType = appraisal.getType();

        for (AppraisalParticipant participant : participants) {
            NewEmployee emp = participant.getParticipant();
            if (emp == null || emp.getEmail() == null) continue;

            String name = emp.getName();
            String email = emp.getEmail();
            String subject;
            String body;

            switch (nextStage) {
                case SELF_REVIEW:
                    subject = "Self Review Phase Started - " + appraisalTitle + " - " + appraisalType;
                    body = emailTemplateService.getSelfReviewStartEmail(name, appraisalTitle, appraisalType);
                    break;

                case REPORTING_REVIEW:
                    subject = "Self Review Ended And Reporting Review Started - " + appraisalTitle + " - " + appraisalType;
                    body = emailTemplateService.getReportingReviewStartEmail(name, appraisalTitle, appraisalType);
                    break;

                case CLOSED:
                    subject = "Appraisal Closed - " + appraisalTitle + " - " + appraisalType;
                    body = emailTemplateService.getAppraisalClosedEmail(name, appraisalTitle, appraisalType);
                    break;

                default:
                    continue; // No email for CREATED or HR_REVIEW stage
            }

            emailService.sendPlainTextMail(email, subject, body);
        }

    }

    private Stage getNextStage(Stage current) {
        switch (current) {
            case CREATED: return Stage.SELF_REVIEW;
            case SELF_REVIEW: return Stage.REPORTING_REVIEW;
            case REPORTING_REVIEW: return Stage.HR_REVIEW;
            case HR_REVIEW: return Stage.CLOSED;
            case CLOSED: return null;
            default: return null;
        }
    }
}
