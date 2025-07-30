package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private double year2022;

    @Column(nullable = false)
    private double year2023;

    @Column(nullable = false)
    private double year2024;

    @Column(nullable = false)
    private double year2025;
}
