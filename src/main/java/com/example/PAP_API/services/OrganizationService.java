package com.example.PAP_API.services;

import com.example.PAP_API.model.Organization;
import com.example.PAP_API.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService{

    @Autowired
    private OrganizationRepository repository;

    public Organization createOrganization(Organization organization) {
        if (repository.existsByCompanyDomain(organization.getCompanyDomain())) {
            throw new RuntimeException("Organization with this domain already exists");
        }
        return repository.save(organization);
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
        org.setCompanyDomain(updatedOrg.getCompanyDomain());
        org.setIndustryType(updatedOrg.getIndustryType());
        org.setOrganizationSize(updatedOrg.getOrganizationSize());
        org.setOfficialEmail(updatedOrg.getOfficialEmail());
        org.setPhoneNumber(updatedOrg.getPhoneNumber());
        org.setAddress(updatedOrg.getAddress());
        org.setCountry(updatedOrg.getCountry());
        return repository.save(org);
    }

    public void deleteOrganization(Long id) {
        repository.deleteById(id);
    }
}
