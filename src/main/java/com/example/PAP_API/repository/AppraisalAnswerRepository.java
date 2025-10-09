package com.example.PAP_API.repository;

import com.example.PAP_API.model.SelfAppraisalAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppraisalAnswerRepository extends JpaRepository<SelfAppraisalAnswer,Long> {
    // Fetch all answers for a participant in a specific appraisal
    List<SelfAppraisalAnswer> findByParticipant_EmployeeIdAndParticipant_AppraisalId(String employeeId, Long appraisalId);
    Optional<SelfAppraisalAnswer> findByParticipantIdAndQuestionId(Long participantId, Long questionId);

}
