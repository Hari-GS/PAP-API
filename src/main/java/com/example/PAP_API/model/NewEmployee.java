package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "new_employees")
@Data
public class NewEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private String email;

    private String password;

    // Self-reference to manager (One manager can manage many employees)
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private NewEmployee manager;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "hr_manager_id", nullable = false) // foreign key column in employees table
    private HRManager hrManager;

    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String gender;
    private String address;
    private String mobileNumber;
}
