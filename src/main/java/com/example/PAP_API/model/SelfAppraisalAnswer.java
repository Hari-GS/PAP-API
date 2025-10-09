package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "question_id"})
)
@Data
public class SelfAppraisalAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answerText;

    private String reportingPersonComment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private AppraisalQuestion question;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private AppraisalParticipant participant;  // helps identify which employee
}
