package com.example.PAP_API.services;

import com.example.PAP_API.dto.*;
import com.example.PAP_API.exception.ResourceNotFoundException;
import com.example.PAP_API.mappers.NewEmployeeMapper;
import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.repository.EmployeeRepository;
import com.example.PAP_API.repository.HRManagerRepository;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.example.PAP_API.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ResponseEntity<List<NewEmployeeDto>> getAllEmployees() {
        Long hrId = userContext.getCurrentUserId();
        List<NewEmployeeDto> employees = employeeRepository.findByHrManagerId(hrId)
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    public ResponseEntity<NewEmployeeDto> getEmployeeById(String employeeId) {
        Long hrManagerId = userContext.getCurrentUserId(); // make sure UserContextService is injected

        NewEmployee employee = employeeRepository.findByEmployeeIdAndHrManagerId(employeeId, hrManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found or access denied."));

        return ResponseEntity.ok(employeeMapper.toDTO(employee));
    }

    public NewEmployee createEmployee(NewEmployeeDto employeeDto, Long hrId, Long organizationId) {
        HRManager hrManager = hrManagerRepository.findById(hrId)
                .orElseThrow(() -> new IllegalArgumentException("HRManager not found."));

        // Check for employeeId duplication under the same HR
        if (employeeRepository.existsByEmployeeIdAndHrManagerId(employeeDto.getEmployeeId(), hrId)) {
            throw new IllegalArgumentException("Employee ID already exists for this HR.");
        }

        // Email still should be globally unique (optional - based on your needs)
        if (employeeRepository.existsByEmailAndHrManagerId(employeeDto.getEmail(), hrId)) {
            throw new IllegalArgumentException("Email ID already exists.");
        }

        NewEmployee employee = employeeMapper.toEntity(employeeDto);
        employee.setHrManager(hrManager);
        employee.setOrganization(organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found.")));

        if (employeeDto.getManagerId() != null) {
            NewEmployee manager = employeeRepository.findByEmployeeIdAndHrManagerId(employeeDto.getManagerId(), hrId)
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found with ID: " + employeeDto.getManagerId()));
            employee.setManager(manager);
        }

        return employeeRepository.save(employee);
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


    @Transactional
    public ResponseEntity<Void> deleteEmployee(String employeeId) {
        Long hrManagerId = userContext.getCurrentUserId();

        NewEmployee employee = employeeRepository.findByEmployeeIdAndHrManagerId(employeeId, hrManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Access denied or employee not found."));

        employeeRepository.delete(employee);
        return ResponseEntity.noContent().build();
    }

    public List<NewEmployeeSummaryDto> getAllEmployeeSummaries(Long hrId) {
        List<NewEmployee> employees = employeeRepository.findByHrManagerId(hrId);
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

}
