package com.example.PAP_API.repository;

import com.example.PAP_API.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByCompanyEmail(String companyEmail);

    Optional<Organization> findByVerificationToken(String token);

    Optional<Organization> findByOrganizationId(String organizationPublicId);
}
