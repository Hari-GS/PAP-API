package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "performance_reviews")
@Data
public class PerformanceReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double score;

    private Boolean completed;

    @ManyToOne
    private Employee employee;
}
