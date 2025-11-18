package com.example.PAP_API.services;

import com.example.PAP_API.dto.ParticipantReportDto;
import com.example.PAP_API.dto.ReviewAnswerDto;
import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.mappers.SelfAppraisalAnswerMapper;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import com.example.PAP_API.repository.AppraisalAnswerRepository;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportingService {

    @Autowired
    AppraisalAnswerRepository selfAppraisalAnswerRepository;

    @Autowired
    private AppraisalParticipantRepository participantRepository;

    private final AppraisalParticipantRepository participantRepo;

    private final AppraisalAnswerRepository appraisalAnswerRepository;

    private final SelfAppraisalAnswerMapper selfAppraisalAnswerMapper;

    public List<AppraisalParticipant> getParticipantsForReporting(Long appraisalId, Long reportingPersonId) {
        return participantRepo.findByAppraisalIdAndReportingPersonId(appraisalId, reportingPersonId);
    }

    public AppraisalParticipant getParticipantDetailsForReporting(Long appraisalId, Long reportingPersonId, Long id) {
        return participantRepo.findByAppraisalIdAndReportingPersonIdAndId(
                appraisalId, reportingPersonId, id
        ).orElseThrow(() -> new EntityNotFoundException("Participant not found"));
    }

    public List<SelfAppraisalAnswerDto> getAnswersForParticipant(Long appraisalId, String employeeId, Long reportingPersonId) {
        List<SelfAppraisalAnswer> answers = appraisalAnswerRepository.findByParticipant_EmployeeIdAndParticipant_AppraisalId(employeeId, appraisalId);
        return selfAppraisalAnswerMapper.toDTOList(answers);
    }

    public List<AppraisalParticipant> getParticipantsByAppraisal(Long appraisalId) {
        return participantRepository.findAllByAppraisalId(appraisalId);
    }

    public ParticipantReportDto getParticipantReport(Long participantId) {
        // 1️⃣ Fetch participant
        AppraisalParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        Appraisal appraisal = participant.getAppraisal();

        // 2️⃣ Prepare DTO
        ParticipantReportDto dto = new ParticipantReportDto();
        dto.setParticipantId(participant.getId());
        dto.setEmployeeId(participant.getEmployeeId());
        dto.setEmployeeName(participant.getEmployeeName());
        dto.setDesignation(participant.getDesignation());
        dto.setManagerName(participant.getManagerName());
        dto.setAppraisalTitle(appraisal.getTitle());
        dto.setAppraisalType(appraisal.getType());
        dto.setAppraisalStage(appraisal.getStage().name());

        // 3️⃣ Fetch answers for this participant
        List<SelfAppraisalAnswer> answers = selfAppraisalAnswerRepository.findByParticipantId(participantId);

        // 4️⃣ Map answers to DTO lists
        List<ReviewAnswerDto> selfReviews = answers.stream()
                .map(a -> {
                    ReviewAnswerDto dtoItem = new ReviewAnswerDto();
                    dtoItem.setQuestion(a.getQuestion().getText());
                    dtoItem.setAnswer(a.getAnswerText());
                    dtoItem.setScore(a.getAnswerScore()); // 🟢 self score
                    return dtoItem;
                })
                .collect(Collectors.toList());

        List<ReviewAnswerDto> reportingReviews = answers.stream()
                .filter(a -> a.getReportingPersonComment() != null || a.getReportingPersonScore() != null)
                .map(a -> {
                    ReviewAnswerDto dtoItem = new ReviewAnswerDto();
                    dtoItem.setQuestion(a.getQuestion().getText());
                    dtoItem.setAnswer(a.getReportingPersonComment());
                    dtoItem.setScore(a.getReportingPersonScore()); // 🟡 manager score
                    return dtoItem;
                })
                .collect(Collectors.toList());

        dto.setSelfReview(selfReviews);
        dto.setReportingReview(reportingReviews);

        // 5️⃣ Compute Self vs Manager Score Comparison
        double avgSelfScore = answers.stream()
                .filter(a -> a.getAnswerScore() != null)
                .mapToDouble(SelfAppraisalAnswer::getAnswerScore)
                .average()
                .orElse(0.0);

        double avgManagerScore = answers.stream()
                .filter(a -> a.getReportingPersonScore() != null)
                .mapToDouble(SelfAppraisalAnswer::getReportingPersonScore)
                .average()
                .orElse(0.0);

        double scoreDifference = avgManagerScore - avgSelfScore;

        // 6️⃣ Compute Manager–Self Agreement %
        double maxScore = 10.0; // Assuming 10-point scale
        double agreementPercentage = 100 - (Math.abs(avgManagerScore - avgSelfScore) / maxScore * 100);
        if (avgSelfScore == 0 && avgManagerScore == 0) {
            agreementPercentage = 0.0;
        }

        dto.setAverageSelfScore(round(avgSelfScore));
        dto.setAverageManagerScore(round(avgManagerScore));
        dto.setScoreDifference(round(scoreDifference));
        dto.setAgreementPercentage(round(agreementPercentage));

        return dto;
    }

    // 🔹 Helper method to round to 2 decimals
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


}
