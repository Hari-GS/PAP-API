package com.example.PAP_API.repository;

import com.example.PAP_API.model.AppraisalQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalQuestionRepository extends JpaRepository<AppraisalQuestion, Long> {
    int countByParticipantId(Long id);
}