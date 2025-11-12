package com.example.PAP_API.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.PAP_API.config.AppProperties;
import com.example.PAP_API.config.UserAuthenticationProvider;
import com.example.PAP_API.dto.*;
import com.example.PAP_API.exception.AppException;
import com.example.PAP_API.exception.ResourceNotFoundException;
import com.example.PAP_API.mappers.NewEmployeeMapper;
import com.example.PAP_API.model.*;
import com.example.PAP_API.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NewEmployeeService {

    @Autowired
    private NewEmployeeRepository employeeRepository;

    @Autowired
    private NewEmployeeMapper employeeMapper;

    @Autowired
    private HRManagerRepository hrManagerRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    UserContextService userContext;

    @Autowired
    UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    AppProperties appProperties;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    @Autowired
    private AppraisalParticipantRepository participantRepository;

    public ResponseEntity<List<NewEmployeeDto>> getAllEmployees() {
        Long hrId = userContext.getCurrentUserId();
        List<NewEmployeeDto> employees = employeeRepository.findByHrManagerId(hrId)
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    public ResponseEntity<NewEmployeeDto> getEmployeeById(String employeeId) {
        Long hrId;
        UserDto user = userContext.getCurrentUser();
        if(user.getRole().equals("director")){
            hrId = employeeRepository.findById(user.getId()).get().getHrManager().getId();
        }else{
            hrId = userContext.getCurrentUserId();
        }

        NewEmployee employee = employeeRepository.findByEmployeeIdAndHrManagerId(employeeId, hrId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found or access denied."));

        return ResponseEntity.ok(employeeMapper.toDTO(employee));
    }

    public NewEmployee createEmployee(NewEmployeeDto employeeDto, Long hrId, Long organizationId) {
        // 1️⃣ Fetch HR and Organization context
        HRManager hrManager = hrManagerRepository.findById(hrId)
                .orElseThrow(() -> new IllegalArgumentException("HR Manager not found."));
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found."));

        // 2️⃣ Validate uniqueness under this HR context
        if (employeeRepository.existsByEmailAndHrManagerId(employeeDto.getEmail(), hrId)) {
            throw new IllegalArgumentException("Email already exists under this HR.");
        }

        // 3️⃣ Map DTO → Entity
        NewEmployee employee = employeeMapper.toEntity(employeeDto);
        employee.setHrManager(hrManager);
        employee.setOrganization(organization);

        // 4️⃣ Set manager if provided
        if (employeeDto.getManagerId() != null) {
            NewEmployee manager = employeeRepository
                    .findByEmployeeIdAndHrManagerId(employeeDto.getManagerId(), hrId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Manager not found with ID: " + employeeDto.getManagerId()));
            employee.setManager(manager);
        }

        // 5️⃣ Initially mark as "INVITED" until signup is complete
        employee.setStatus("INVITED");

        // 6️⃣ Generate a secure JWT token (for signup link)
        String token = userAuthenticationProvider.generateInviteToken(
                employee.getEmail(),
                employee.getEmployeeId(),
                employee.getName(),
                hrManager.getId(),
                organization.getId()
        );

        employee.setSignupToken(token); // optional field in DB for tracking
        employee.setTokenExpiry(LocalDateTime.now().plusHours(24)); // 24 hours validity
        employeeRepository.save(employee);

        // 7️⃣ Build the signup invite link
        String signupUrl = appProperties.getAllowedFrontendOrigin()+"/signup-participant?token=" + token;

        // 8️⃣ Build email content
        String htmlContent = emailTemplateService.getParticipantInviteEmail(
                employee.getName(),
                hrManager.getName(),
                organization.getOrganizationName(),
                signupUrl
        );

        // 9️⃣ Send invite email
        emailService.sendHtmlMail(
                employee.getEmail(),
                "You’ve Been Invited to " + organization.getOrganizationName() + " Appraisal Portal",
                htmlContent
        );

        return employee;
    }

    public ResponseEntity<NewEmployeeDto> updateEmployee(String employeeId, NewEmployeeDto updatedDTO) {
        Long hrManagerId = userContext.getCurrentUserId(); // Make sure UserContextService is injected

        NewEmployee existing = employeeRepository.findByEmployeeIdAndHrManagerId(employeeId, hrManagerId)
                .orElseThrow(() -> new IllegalArgumentException("Access denied or Employee not found with ID: " + employeeId));

        // Update fields
        employeeMapper.updateEmployeeFromDto(updatedDTO, existing);

        // Handle manager reference separately
        if (updatedDTO.getManagerId() != null) {
            NewEmployee manager = employeeRepository.findByEmployeeIdAndHrManagerId(updatedDTO.getManagerId(),hrManagerId)
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found with ID: " + updatedDTO.getManagerId()));
            existing.setManager(manager);
        } else {
            existing.setManager(null);
        }

        NewEmployee updated = employeeRepository.save(existing);

        return ResponseEntity.ok(employeeMapper.toDTO(updated));
    }

    public ResponseEntity<NewEmployeeDto> activeEmployee(String employeeId) {
        Long hrManagerId = userContext.getCurrentUserId(); // Make sure UserContextService is injected

        NewEmployee employee = employeeRepository.findEmployeeByEmployeeIdAndHrManagerIdNative(employeeId, hrManagerId)
                .orElseThrow(() -> new IllegalArgumentException("Access denied or Employee not found with ID: " + employeeId));
        employee.setStatus("ACTIVE");
        NewEmployee updated = employeeRepository.save(employee);

        return ResponseEntity.ok(employeeMapper.toDTO(updated));
    }


    @Transactional
    public ResponseEntity<Void> deleteEmployee(String employeeId) {
        Long hrManagerId = userContext.getCurrentUserId();

        NewEmployee employee = employeeRepository.findByEmployeeIdAndHrManagerId(employeeId, hrManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Access denied or employee not found."));

        employeeRepository.delete(employee);
        return ResponseEntity.noContent().build();
    }

    public List<NewEmployeeSummaryDto> getAllEmployeeSummaries(Long hrId) {
        List<NewEmployee> employees = employeeRepository.findActiveEmployeesByHrManagerIdNative(hrId);
        return employeeMapper.toSummaryDTOs(employees);
    }

    public List<NewEmployeeSummaryDto> getAllInactiveEmployeeSummaries(Long hrId) {
        List<NewEmployee> employees = employeeRepository.findInactiveEmployeesByHrManagerIdNative(hrId);

//        if (employees.isEmpty()) {
//            throw new AppException("No inactive employees found", HttpStatus.NOT_FOUND);
//        }

        return employeeMapper.toSummaryDTOs(employees);
    }


    public List<EmployeeHierarchyDto> getEmployeeHierarchy(Long hrId) {
        List<NewEmployee> employees = employeeRepository.findByHrManagerId(hrId);

        return employees.stream().map(emp -> {
            EmployeeHierarchyDto dto = new EmployeeHierarchyDto();
            dto.setEmployeeId(emp.getEmployeeId());
            dto.setName(emp.getName());
            dto.setDesignation(emp.getDesignation());
            dto.setManagerId(emp.getManager() != null ? emp.getManager().getEmployeeId() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<EmployeeAppraisalSummaryDto> getAppraisalsForEmployee(Long id) {
        NewEmployee employee = employeeRepository.findById(id).get();
//        List<AppraisalParticipant> participants = participantRepository.findByEmployeeId(employee.getEmployeeId());
        List<AppraisalParticipant> participants = participantRepository.findByParticipantId(employee.getId());

        return participants.stream().map(p -> {
            Appraisal a = p.getAppraisal();
            EmployeeAppraisalSummaryDto dto = new EmployeeAppraisalSummaryDto();
            dto.setAppraisalId(a.getId());
            dto.setTitle(a.getTitle());
            dto.setStatus(p.getSelfAppraisalStatus().toString());
            dto.setType(a.getType());
            dto.setStartDate(a.getStartDate().toString());
            dto.setSelfAppraisalEndDate(a.getSelfAppraisalEndDate().toString());
            dto.setEndDate(a.getEndDate().toString());
            dto.setStage(a.getStage().name());
            dto.setCreatedAt(a.getCreatedAt().toString());
            dto.setCreatedBy(a.getHrManager().getName());
            dto.setTotalSelfQns(p.getTotalQns());
            dto.setSelfQnsAnswered(p.getTotalQnsAnswered());
            return dto;
        }).toList();
    }

    public List<EmployeeAppraisalSummaryDto> getClosedAppraisalsForEmployee(Long id) {
        NewEmployee employee = employeeRepository.findById(id).get();
//        List<AppraisalParticipant> participants = participantRepository.findByEmployeeId(employee.getEmployeeId());
        List<AppraisalParticipant> participants = participantRepository.findClosedAppraisalsByEmployeeId(id);

        return participants.stream().map(p -> {
            Appraisal a = p.getAppraisal();
            EmployeeAppraisalSummaryDto dto = new EmployeeAppraisalSummaryDto();
            dto.setAppraisalId(a.getId());
            dto.setTitle(a.getTitle());
            dto.setStatus(p.getSelfAppraisalStatus().toString());
            dto.setType(a.getType());
            dto.setStartDate(a.getStartDate().toString());
            dto.setSelfAppraisalEndDate(a.getSelfAppraisalEndDate().toString());
            dto.setEndDate(a.getEndDate().toString());
            dto.setStage(a.getStage().name());
            dto.setCreatedAt(a.getCreatedAt().toString());
            dto.setCreatedBy(a.getHrManager().getName());
            dto.setTotalSelfQns(p.getTotalQns());
            dto.setSelfQnsAnswered(p.getTotalQnsAnswered());
            return dto;
        }).toList();
    }

    public List<EmployeeAppraisalSummaryDto> getActiveAppraisalsForEmployee(Long id) {
        NewEmployee employee = employeeRepository.findById(id).get();
//        List<AppraisalParticipant> participants = participantRepository.findByEmployeeId(employee.getEmployeeId());
        List<AppraisalParticipant> participants = participantRepository.findActiveAppraisals(id);

        return participants.stream().map(p -> {
            Appraisal a = p.getAppraisal();
            EmployeeAppraisalSummaryDto dto = new EmployeeAppraisalSummaryDto();
            dto.setAppraisalId(a.getId());
            dto.setTitle(a.getTitle());
            dto.setStatus(p.getSelfAppraisalStatus().toString());
            dto.setType(a.getType());
            dto.setStartDate(a.getStartDate().toString());
            dto.setSelfAppraisalEndDate(a.getSelfAppraisalEndDate().toString());
            dto.setEndDate(a.getEndDate().toString());
            dto.setStage(a.getStage().name());
            dto.setCreatedAt(a.getCreatedAt().toString());
            dto.setCreatedBy(a.getHrManager().getName());
            dto.setTotalSelfQns(p.getTotalQns());
            dto.setSelfQnsAnswered(p.getTotalQnsAnswered());
            return dto;
        }).toList();
    }

    public void completeSignup(String token, String password) {
        // Verify JWT integrity and expiration
        DecodedJWT decoded;
        try {
            decoded = userAuthenticationProvider.validateInviteToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token.");
        }

        // Fetch DB record
        NewEmployee user = employeeRepository.findBySignupToken(token)
                .orElseThrow(() -> new RuntimeException("No user found for this token."));

        // Check DB expiry too (redundant safety)
        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired. Please request a new invite.");
        }

        // Activate account
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus("ACTIVE");
        user.setSignupToken(null);
        user.setTokenExpiry(null);
        employeeRepository.save(user);
    }


}
