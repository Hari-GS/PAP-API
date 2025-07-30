package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private String publicId = UUID.randomUUID().toString();

    private String organizationName;
    private String companyDomain;
    private String industryType;
    private String organizationSize;
    private String officialEmail;
    private String phoneNumber;
    private String address;
    private String country;
}
