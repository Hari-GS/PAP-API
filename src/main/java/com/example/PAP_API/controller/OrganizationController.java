package com.example.PAP_API.controller;

import com.example.PAP_API.config.AppProperties;
import com.example.PAP_API.model.Organization;
import com.example.PAP_API.repository.OrganizationRepository;
import com.example.PAP_API.services.EmailService;
import com.example.PAP_API.services.EmailTemplateService;
import com.example.PAP_API.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auth/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    EmailTemplateService emailTemplateService;

    @Autowired
    EmailService emailService;

    @Autowired
    AppProperties appProperties;

    @PostMapping
    public Organization create(@RequestBody Organization organization) {
        return organizationService.createOrganization(organization);
    }

    @GetMapping("/{id}")
    public Organization getById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id);
    }

    @GetMapping
    public List<Organization> getAll() {
        return organizationService.getAllOrganizations();
    }

    @PutMapping("/{id}")
    public Organization update(@PathVariable Long id, @RequestBody Organization organization) {
        return organizationService.updateOrganization(id, organization);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
    }

    @GetMapping("/verify")
    public RedirectView verifyOrganization(@RequestParam("token") String token) {
        Organization org = repository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token."));

        if (org.getTokenExpiry().isBefore(LocalDateTime.now())) {
            // Redirect with status message for expired token
            return new RedirectView(appProperties.getLoginUrl()+"?status=expired");
        }

        org.setStatus("VERIFIED");
        org.setVerificationToken(null); // clear token after success
        org.setTokenExpiry(null);
        repository.save(org);

        //Sending email
        String body = emailTemplateService.getOrganizationVerifiedEmailTemplate(org.getOrganizationName(), org.getOrganizationId());
        emailService.sendHtmlMail(org.getCompanyEmail(),"Your Organization Verified Successfully!",body);

        // Redirect to frontend with success query parameter
        return new RedirectView(appProperties.getLoginUrl()+"?status=success");
    }

}
