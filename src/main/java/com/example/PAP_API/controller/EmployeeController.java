package com.example.PAP_API.controller;

import com.example.PAP_API.dto.EmployeeDto;
import com.example.PAP_API.dto.EmployeeFullDto;
import com.example.PAP_API.dto.EmployeeSummaryDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.mappers.EmployeeMapper;
import com.example.PAP_API.model.Employee;
import com.example.PAP_API.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/summary")
    public ResponseEntity<List<EmployeeSummaryDto>> getAllEmployees(Authentication authentication) {
        // Assuming you stored your UserDto in the principal during authentication
        UserDto user = (UserDto) authentication.getPrincipal();
        Long hrId = user.getId();  // Now this will give HR's id'

        List<EmployeeSummaryDto> employees = employeeService.getAllEmployeeSummaries(hrId);
        return ResponseEntity.ok(employees);
    }


    // Get employee by ID
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeFullDto> getEmployeeById(@PathVariable String employeeId) {
        EmployeeFullDto employeeFullDto = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employeeFullDto);
    }


    @PostMapping("/employee")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDto employeeDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long hrId;
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDto) {
                hrId = ((UserDto) principal).getId(); // or getUsername() if you named it that
            } else {
                throw new RuntimeException("Unexpected principal type: " + principal.getClass());
            }

            Employee saved = employeeService.createEmployee(employeeDto, hrId);

            return ResponseEntity.ok(employeeMapper.toDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/employee/{employeeId}")
    public ResponseEntity<?> updateEmployee(@PathVariable String employeeId, @RequestBody EmployeeDto employeeDto) {
        try {
            Optional<Employee> updated = employeeService.updateEmployee(employeeId, employeeDto);
            return updated.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Delete an employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.deleteEmployee(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadEmployeesCSV(@RequestParam("file") MultipartFile file) {
        try {
            String result = employeeService.processCSVUpload(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

}
