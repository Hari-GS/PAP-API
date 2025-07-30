package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AppraisalQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private boolean showPoints;
    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private AppraisalParticipant participant;
}
