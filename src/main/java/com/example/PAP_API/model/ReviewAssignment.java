package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id")
    private ReviewCycles cycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    private Employee reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewer_type")
    private ReviewerType reviewerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private Form form;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    public enum ReviewerType {
        SELF, MANAGER, PEER, SUBORDINATE, CLIENT
    }

    public enum Status {
        PENDING, SUBMITTED
    }
}
