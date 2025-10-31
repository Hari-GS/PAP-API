package com.example.PAP_API.services;

import com.example.PAP_API.config.AppProperties;
import com.example.PAP_API.model.Organization;
import com.example.PAP_API.repository.OrganizationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizationService{

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    private final AppProperties appProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Organization createOrganization(Organization organization) {
        // --- 1. Check for duplicate company email ---
        if (repository.existsByCompanyEmail(organization.getCompanyEmail())) {
            throw new RuntimeException("An organization with this official email already exists.");
        }

        // --- 2. Generate a unique, readable 12-digit Organization ID ---
        String organizationId = "ORG-" + String.format("%012d", Math.abs(new Random().nextLong() % 1_000_000_000_000L));
        organization.setOrganizationId(organizationId);

        // --- Generate verification token (UUID) ---
        String token = UUID.randomUUID().toString();
        organization.setVerificationToken(token);
        organization.setTokenExpiry(LocalDateTime.now().plusHours(24)); // expires in 24 hrs

        // --- 3. Set default fields ---
        organization.setStatus("PENDING_VERIFICATION");
        organization.setCreatedAt(LocalDateTime.now());
        organization.setPassword(passwordEncoder.encode(organization.getPassword()));

        // --- 4. Save to DB ---
        Organization savedOrg = repository.save(organization);

        // --- 6. Build verification link ---
        String verificationLink = appProperties.getBackendUrl()+"/auth/organization/verify?token=" + token;

        // --- 5. Send verification email (optional but professional) ---
        String subject = "Please Verify Your Organization!";
        String body = emailTemplateService.getOrganizationVerificationEmailTemplate(savedOrg.getOrganizationName(),verificationLink);
        emailService.sendHtmlMail(savedOrg.getCompanyEmail(), subject,body);

        return savedOrg;
    }

    public Organization getOrganizationById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    public List<Organization> getAllOrganizations() {
        return repository.findAll();
    }

    public Organization updateOrganization(Long id, Organization updatedOrg) {
        Organization org = getOrganizationById(id);
        org.setOrganizationName(updatedOrg.getOrganizationName());
        org.setCompanyEmail(updatedOrg.getCompanyEmail());
        org.setIndustryType(updatedOrg.getIndustryType());
        org.setContactEmail(updatedOrg.getContactEmail());
        return repository.save(org);
    }

    public void deleteOrganization(Long id) {
        repository.deleteById(id);
    }
}
