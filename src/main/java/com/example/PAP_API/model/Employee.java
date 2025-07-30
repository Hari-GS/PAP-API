package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String employeeId;
    private String password;
    private String designation;
    private int roleId;

    @ManyToOne
    @JoinColumn(name = "hr_manager_id", nullable = false) // foreign key column in employees table
    private HRManager hrManager;

    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String gender;
    private String address;
    private String mobileNumber;
}
