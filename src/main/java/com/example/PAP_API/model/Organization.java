package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false, length = 20)
    private String organizationId;

    private String organizationName;
    private String companyEmail;
    private String industryType;
    private String contactEmail;
    private String password;
    private String status;          // PENDING_VERIFICATION / VERIFIED / ACTIVE
    private LocalDateTime createdAt;

    private String verificationToken;
    private LocalDateTime tokenExpiry;

//    public Organization() {
//        this.organizationId = generateOrganizationId();
//    }

    private String generateOrganizationId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("ORG-");
        Random random = new Random();
        for (int i = 0; i < 8; i++) { // 8 chars + prefix = 12 total
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

}
