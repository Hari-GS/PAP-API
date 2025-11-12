package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "new_employees")
@Data
@SQLDelete(sql = "UPDATE new_employees SET status = 'INACTIVE' WHERE id = ?")
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

    @Column(length = 512)
    private String signupToken;

    @Column(nullable = false)
    private String status; // INVITED, ACTIVE, INACTIVE, etc.
    private LocalDateTime tokenExpiry;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private NewEmployee manager;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "hr_manager_id", nullable = false)
    private HRManager hrManager;

    private Boolean isDirector;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String gender;
    private String address;
    private String mobileNumber;
}
