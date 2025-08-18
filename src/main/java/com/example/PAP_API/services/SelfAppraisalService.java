package com.example.PAP_API.services;

import com.example.PAP_API.dto.AppraisalQuestionDto;
import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.enums.Statuses;
import com.example.PAP_API.mappers.SelfAppraisalAnswerMapper;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.AppraisalQuestion;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import com.example.PAP_API.repository.AppraisalAnswerRepository;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import com.example.PAP_API.repository.AppraisalQuestionRepository;
import com.example.PAP_API.repository.NewEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

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

        // Step 1: Get employeeId from logged-in user
        String empId = employeeRepository.findById(userParticipant.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"))
                .getEmployeeId();

        // Step 2: Find the participant record for this appraisal
        AppraisalParticipant participant = participantRepo
                .findByAppraisalIdAndEmployeeId(appraisalId, empId)
                .orElseThrow(() -> new RuntimeException("Participant not found for this appraisal"));

        List<SelfAppraisalAnswer> answersToSave = new ArrayList<>();

        // Step 3: For each incoming answer
        for (SelfAppraisalAnswerDto dto : answerDtos) {

            // Find the question entity
            AppraisalQuestion question = questionRepo.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            // Check if an answer already exists for this participant & question
            SelfAppraisalAnswer answer = answerRepo
                    .findByParticipantIdAndQuestionId(participant.getId(), dto.getQuestionId())
                    .orElseGet(SelfAppraisalAnswer::new);

            // If new, set participant & question
            if (answer.getId() == null) {
                answer.setParticipant(participant);
                answer.setQuestion(question);
            }

            // Update the answer text
            answer.setAnswerText(dto.getAnswerText());

            answersToSave.add(answer);
        }

        // Step 4: Save all (insert new / update existing)
        answerRepo.saveAll(answersToSave);

        // Step 5: Update participant status & answered count
        participant.setTotalQnsAnswered(answersToSave.size());
        participant.setSelfAppraisalStatus(Statuses.SUBMITTED);
        participantRepo.save(participant);
    }


    public List<AppraisalQuestionDto> getSelfAppraisalQuestions(Long appraisalId, UserDto userparticipant) {
        String employeeId = employeeRepository.findById(userparticipant.getId())
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
        String empId = employeeRepository.findById(userParticipant.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"))
                .getEmployeeId();
        return selfAppraisalAnswerMapper.toDTOList(answerRepo.findByParticipant_EmployeeIdAndParticipant_AppraisalId(empId, appraisalId));


    }

}
