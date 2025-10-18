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
        dto.setAppraisalStage(appraisal.getStage().name());

        // 3️⃣ Fetch answers for this participant
        List<SelfAppraisalAnswer> answers = selfAppraisalAnswerRepository.findByParticipantId(participantId);

        // 4️⃣ Map answers to DTO lists
        List<ReviewAnswerDto> selfReviews = answers.stream()
                .map(a -> new ReviewAnswerDto(
                        a.getQuestion().getText(),
                        a.getAnswerText()
                ))
                .collect(Collectors.toList());

        List<ReviewAnswerDto> reportingReviews = answers.stream()
                .filter(a -> a.getReportingPersonComment() != null && !a.getReportingPersonComment().isEmpty())
                .map(a -> new ReviewAnswerDto(
                        a.getQuestion().getText(),
                        a.getReportingPersonComment()
                ))
                .collect(Collectors.toList());

        dto.setSelfReview(selfReviews);
        dto.setReportingReview(reportingReviews);

        return dto;
    }

}
