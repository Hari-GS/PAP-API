package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "review_cycles")
public class ReviewCycles {
    @Id
    private int id;
    private String name;  //e.g., 'Q1 2025 Review'
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    public enum Status {
        DRAFT,
        ACTIVE,
        CLOSED
    }
}
