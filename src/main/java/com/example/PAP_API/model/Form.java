package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_role_id")
    private Roles revieweeRole;

    @Enumerated(EnumType.STRING)
    private ReviewerType reviewerType;

    @Column(name = "created_by")
    private Integer createdBy; // HR user ID

    public enum ReviewerType {
        SELF, MANAGER, PEER, SUBORDINATE, CLIENT
    }
}
