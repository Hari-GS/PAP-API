package com.example.PAP_API.services;

import com.example.PAP_API.dto.AppraisalQuestionDto;
import com.example.PAP_API.dto.ReportingPersonDto;
import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.enums.Statuses;
import com.example.PAP_API.exception.AppException;
import com.example.PAP_API.mappers.SelfAppraisalAnswerMapper;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.AppraisalQuestion;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import com.example.PAP_API.repository.AppraisalAnswerRepository;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import com.example.PAP_API.repository.AppraisalQuestionRepository;
import com.example.PAP_API.repository.NewEmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SelfAppraisalService {

    @Autowired
    AppraisalParticipantRepository participantRepo;

    @Autowired
    AppraisalQuestionRepository questionRepo;

    @Autowired
    NewEmployeeRepository employeeRepository;

    @Autowired
    AppraisalAnswerRepository answerRepo;

    @Autowired
    SelfAppraisalAnswerMapper selfAppraisalAnswerMapper;

    public void saveAnswers(Long appraisalId, UserDto userParticipant, List<SelfAppraisalAnswerDto> answerDtos) {

        Long id;

        if ("hr".equalsIgnoreCase(userParticipant.getRole())) {
            NewEmployee newEmployee = employeeRepository.findByEmail(userParticipant.getEmail())
                    .orElseThrow(() -> new AppException("Employee not found", HttpStatus.NOT_FOUND));
            id = newEmployee.getId();
        } else {
            id = userParticipant.getId();
        }

        // Step 1: Get employeeId from logged-in user
        String empId = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"))
                .getEmployeeId();

        // Step 2: Find the participant record for this appraisal
        AppraisalParticipant participant = participantRepo
                .findByAppraisalIdAndEmployeeId(appraisalId, empId)
                .orElseThrow(() -> new RuntimeException("Participant not found for this appraisal"));

        List<SelfAppraisalAnswer> answersToSave = new ArrayList<>();

        // Step 3: For each incoming answer
        for (SelfAppraisalAnswerDto dto : answerDtos) {

            AppraisalQuestion question = questionRepo.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            SelfAppraisalAnswer answer = answerRepo
                    .findByParticipantIdAndQuestionId(participant.getId(), dto.getQuestionId())
                    .orElseGet(SelfAppraisalAnswer::new);

            if (answer.getId() == null) {
                answer.setParticipant(participant);
                answer.setQuestion(question);
            }

            // Only set non-empty answers; treat empty as unanswered
            String ansText = dto.getAnswerText();
            if (ansText != null && !ansText.trim().isEmpty()) {
                answer.setAnswerText(ansText.trim());
            } else {
                answer.setAnswerText(null); // store as null for unanswered
            }

            // Handle score field
            if (dto.getAnswerScore() != null) {
                answer.setAnswerScore(dto.getAnswerScore());
            } else {
                answer.setAnswerScore(null);
            }


            answersToSave.add(answer);
        }

        // Step 4: Save all (insert new / update existing)
        answerRepo.saveAll(answersToSave);

        // Step 5: Determine status based on answered (non-empty) count
        int totalQuestions = questionRepo.countByParticipantId(participant.getId());
        int answeredCount = answerRepo.countAnsweredQuestionsByParticipant(participant.getId());


        participant.setTotalQnsAnswered(answeredCount);

        if (answeredCount == 0) {
            participant.setSelfAppraisalStatus(Statuses.NOT_STARTED);
        } else if (answeredCount < totalQuestions) {
            participant.setSelfAppraisalStatus(Statuses.IN_PROGRESS);
        } else {
            participant.setSelfAppraisalStatus(Statuses.SUBMITTED);
        }

        participantRepo.save(participant);
    }


    public List<AppraisalQuestionDto> getSelfAppraisalQuestions(Long appraisalId, UserDto userparticipant) {
        Long id;

        if ("hr".equalsIgnoreCase(userparticipant.getRole())) {
            NewEmployee newEmployee = employeeRepository.findByEmail(userparticipant.getEmail())
                    .orElseThrow(() -> new AppException("Employee not found", HttpStatus.NOT_FOUND));
            id = newEmployee.getId();
        } else {
            id = userparticipant.getId();
        }

        String employeeId = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"))
                .getEmployeeId();

        AppraisalParticipant participant = participantRepo
                .findByAppraisalIdAndEmployeeId(appraisalId, employeeId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        return participant.getQuestions().stream()
                .map(q -> {
                    AppraisalQuestionDto dto = new AppraisalQuestionDto();
                    dto.setId(q.getId());
                    dto.setText(q.getText());
                    dto.setShowPoints(q.isShowPoints());
                    dto.setOrderIndex(q.getOrderIndex());
                    return dto;
                }).toList();
    }

    public List<SelfAppraisalAnswerDto> getAnswersByParticipant(Long appraisalId, UserDto userParticipant) {
        Long id;

        if ("hr".equalsIgnoreCase(userParticipant.getRole())) {
            NewEmployee newEmployee = employeeRepository.findByEmail(userParticipant.getEmail())
                    .orElseThrow(() -> new AppException("Employee not found", HttpStatus.NOT_FOUND));
            id = newEmployee.getId();
        } else {
            id = userParticipant.getId();
        }

        String empId = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"))
                .getEmployeeId();
        return selfAppraisalAnswerMapper.toDTOList(answerRepo.findByParticipant_EmployeeIdAndParticipant_AppraisalId(empId, appraisalId));


    }

    public List<SelfAppraisalAnswerDto> addReportingPersonComment(List<ReportingPersonDto> dtos) {
        List<SelfAppraisalAnswerDto> updatedAnswers = new ArrayList<>();
        Set<Long> participantIds = new HashSet<>();

        for (ReportingPersonDto dto : dtos) {
            SelfAppraisalAnswer answer = answerRepo.findById(dto.getAnswerId())
                    .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + dto.getAnswerId()));

            participantIds.add(answer.getParticipant().getId());

            // ✅ Validate and set manager's inputs
            if (dto.getReportingPersonScore() != null &&
                    (dto.getReportingPersonScore() < 1 || dto.getReportingPersonScore() > 10)) {
                throw new IllegalArgumentException("Reporting person score must be between 1 and 10");
            }

            answer.setReportingPersonComment(dto.getReportingPersonComment());
            answer.setReportingPersonScore(dto.getReportingPersonScore());

            SelfAppraisalAnswer saved = answerRepo.save(answer);
            updatedAnswers.add(selfAppraisalAnswerMapper.toDTO(saved));
        }

        // ✅ Update participant review status
        for (Long participantId : participantIds) {
            AppraisalParticipant participant = participantRepo.findById(participantId)
                    .orElseThrow(() -> new EntityNotFoundException("Participant not found with id: " + participantId));

            List<SelfAppraisalAnswer> answers = answerRepo.findByParticipantId(participantId);

            // ✅ Count answers where BOTH comment & score are provided
            long reviewedCount = answers.stream()
                    .filter(a ->
                            a.getReportingPersonScore() != null &&
                                    a.getReportingPersonComment() != null &&
                                    !a.getReportingPersonComment().trim().isEmpty()
                    )
                    .count();

            // ✅ Update review progress status
            if (reviewedCount == 0) {
                participant.setReviewAppraisalStatus(Statuses.NOT_STARTED);
            } else if (reviewedCount < answers.size()) {
                participant.setReviewAppraisalStatus(Statuses.IN_PROGRESS);
            } else {
                participant.setReviewAppraisalStatus(Statuses.SUBMITTED);
            }

            participantRepo.save(participant);
        }

        return updatedAnswers;
    }



    public List<AppraisalParticipant> getParticipantsForReporting(Long appraisalId, Long reportingPersonId) {
        return participantRepo.findByAppraisalIdAndReportingPersonId(appraisalId, reportingPersonId);
    }
}
