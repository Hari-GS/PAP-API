package com.example.PAP_API.controller;

import com.example.PAP_API.dto.*;
import com.example.PAP_API.exception.ResourceNotFoundException;
import com.example.PAP_API.mappers.NewEmployeeMapper;
import com.example.PAP_API.mappers.UserMapper;
import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.example.PAP_API.services.NewEmployeeService;
import com.example.PAP_API.services.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
import java.util.List;

@RestController
@RequestMapping("/api/new-employees")
public class NewEmployeeController {

    @Autowired
    private NewEmployeeService employeeService;

    @Autowired
    private NewEmployeeMapper employeeMapper;

    @Autowired
    private UserContextService userContext;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NewEmployeeRepository newEmployeeRepository;

    @GetMapping
    public ResponseEntity<List<NewEmployeeDto>> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewEmployeeDto> getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<NewEmployeeSummaryDto>> getAllEmployeesSummaries() {
        // Assuming you stored your UserDto in the principal during authentication
        ;
        Long hrId = userContext.getCurrentUserId();
        System.out.println(userContext.getCurrentUser().toString());
        List<NewEmployeeSummaryDto> employees = employeeService.getAllEmployeeSummaries(hrId);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody NewEmployeeDto employeeDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long hrId;
            Long organizationId;
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDto) {
                hrId = ((UserDto) principal).getId(); // or getUsername() if you named it that

                organizationId = ((UserDto) principal).getOrganization();
            } else {
                throw new RuntimeException("Unexpected principal type: " + principal.getClass());
            }

            NewEmployee saved = employeeService.createEmployee(employeeDto, hrId,organizationId );

            return ResponseEntity.ok(employeeMapper.toDTO(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<NewEmployeeDto> updateEmployee(@PathVariable String employeeId,
                                                      @RequestBody NewEmployeeDto employeeDTO) {
        return employeeService.updateEmployee(employeeId, employeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @GetMapping("/by-hr/hierarchy")
    public ResponseEntity<List<EmployeeHierarchyDto>> getHierarchy(Authentication authentication) {
        UserDto user = (UserDto) authentication.getPrincipal();
        Long hrId = user.getId();
        return ResponseEntity.ok(employeeService.getEmployeeHierarchy(hrId));
    }

    @GetMapping("/me")
    public ResponseEntity<WelcomeCardDto> getCurrentUser(@AuthenticationPrincipal UserDto employee) {
        WelcomeCardDto hrWelcomeCardDto = userMapper.toWelcomeCardDto(employee);
        return ResponseEntity.ok(hrWelcomeCardDto);
    }

    @GetMapping("/appraisals")
    public ResponseEntity<List<EmployeeAppraisalSummaryDto>> getMyAppraisals(@AuthenticationPrincipal UserDto employee) {
        List<EmployeeAppraisalSummaryDto> appraisals = employeeService.getAppraisalsForEmployee(employee.getId());
        return ResponseEntity.ok(appraisals);
    }

    @GetMapping("/evaluators")
    public ResponseEntity<NewEmployeeDto> getMyEvaluators(@AuthenticationPrincipal UserDto employee){
        NewEmployeeDto dto = new NewEmployeeDto();
        NewEmployee currentEmployee = newEmployeeRepository.findById(employee.getId())
                .orElseThrow(()->new ResourceNotFoundException("Employee Not Found"));
        dto.setMyHrManager(currentEmployee.getHrManager().getName());

        dto.setReportingPerson(
                currentEmployee.getManager() != null
                        ? currentEmployee.getManager().getName()
                        : "_"
        );

        dto.setReportingPersonDesignation(
                currentEmployee.getManager() != null
                        ? currentEmployee.getManager().getDesignation()
                        : "_"
        );

        return ResponseEntity.ok(dto);
    }
}
