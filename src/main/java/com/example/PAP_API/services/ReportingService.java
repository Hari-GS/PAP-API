package com.example.PAP_API.services;

import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.mappers.SelfAppraisalAnswerMapper;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import com.example.PAP_API.repository.AppraisalAnswerRepository;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {

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


}
