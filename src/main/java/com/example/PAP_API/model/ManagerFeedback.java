package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ManagerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private Integer points; // optional rating out of 5/10 etc.

    @ManyToOne
    @JoinColumn(name = "question_id")
    private AppraisalQuestion question;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private AppraisalParticipant participant;  // manager reviewing this participant
}
