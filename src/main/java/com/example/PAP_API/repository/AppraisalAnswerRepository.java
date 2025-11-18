package com.example.PAP_API.repository;

import com.example.PAP_API.model.SelfAppraisalAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppraisalAnswerRepository extends JpaRepository<SelfAppraisalAnswer,Long> {
    // Fetch all answers for a participant in a specific appraisal
    List<SelfAppraisalAnswer> findByParticipant_EmployeeIdAndParticipant_AppraisalId(String employeeId, Long appraisalId);
    Optional<SelfAppraisalAnswer> findByParticipantIdAndQuestionId(Long participantId, Long questionId);

    int countByParticipantId(Long id);

    List<SelfAppraisalAnswer> findByParticipantId(Long participantId);

    @Query("""
    SELECT COUNT(a) 
    FROM SelfAppraisalAnswer a 
    WHERE a.participant.id = :participantId
      AND a.answerText IS NOT NULL 
      AND a.answerText <> '' 
      AND a.answerScore IS NOT NULL
    """)
    int countAnsweredQuestionsByParticipant(@Param("participantId") Long participantId);

}
