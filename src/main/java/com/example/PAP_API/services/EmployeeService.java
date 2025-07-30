package com.example.PAP_API.services;

import com.example.PAP_API.dto.EmployeeDto;
import com.example.PAP_API.dto.EmployeeFullDto;
import com.example.PAP_API.dto.EmployeeSummaryDto;
import com.example.PAP_API.exception.ResourceNotFoundException;
import com.example.PAP_API.mappers.EmployeeMapper;
import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.repository.EmployeeRepository;
import com.example.PAP_API.repository.HRManagerRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    HRManagerRepository hrManagerRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public List<EmployeeSummaryDto> getAllEmployeeSummaries(Long id) {
        List<Employee> employees = employeeRepository.findByHrManagerId(id);
        return employeeMapper.toSummaryDTOs(employees);
    }

    // Fetch all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Fetch employee by ID
    public EmployeeFullDto getEmployeeById(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + employeeId));
        return employeeMapper.toFullDto(employee);
    }


    public Employee createEmployee(EmployeeDto employeeDto, Long id) {
        if (employeeRepository.existsByEmployeeId(employeeDto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists.");
        }

        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new IllegalArgumentException("Email ID already exists.");
        }

        HRManager hrManager = hrManagerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HRManager not found."));

        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setHrManager(hrManager); // 🔗 Set relationship

        return employeeRepository.save(employee);
    }



    // Update an employee
    public Optional<Employee> updateEmployee(String employeeId, EmployeeDto employeeDetails) {
        return employeeRepository.findByEmployeeId(employeeId).map(existingEmployee -> {

            // Check email uniqueness only if it's being changed
            if (!existingEmployee.getEmail().equals(employeeDetails.getEmail()) &&
                    employeeRepository.existsByEmail(employeeDetails.getEmail())) {
                throw new IllegalArgumentException("Email ID already exists.");
            }

            if (!existingEmployee.getEmployeeId().equals(employeeDetails.getEmployeeId()) &&
                    employeeRepository.existsByEmployeeId(employeeDetails.getEmployeeId())) {
                throw new IllegalArgumentException("Employee ID already exists.");
            }

            // Map DTO to entity (except password if not provided)
            existingEmployee.setName(employeeDetails.getName());
            existingEmployee.setEmail(employeeDetails.getEmail());
            existingEmployee.setDesignation(employeeDetails.getDesignation());

            return employeeRepository.save(existingEmployee);
        });
    }


    // Delete an employee
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public String getEmployeeNameById(String id) {
        return employeeRepository.findByEmployeeId(id)
                .map(Employee::getName)
                .orElse("Unknown");
    }

    public String  processCSVUpload(MultipartFile file) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());

        StringBuilder skipped = new StringBuilder();

        for (CSVRecord csvRecord : csvParser) {
            String employeeId = csvRecord.get("employeeId");
            String emailId = csvRecord.get("emailId");

            boolean duplicate = employeeRepository.existsByEmployeeId(employeeId)
                    || employeeRepository.existsByEmail(emailId);

            if (duplicate) {
                skipped.append("Skipped duplicate - ID: ").append(employeeId)
                        .append(", Email: ").append(emailId);
                continue;
            }

            String name = csvRecord.get("name");
            String password = csvRecord.get("password");
            String designation = csvRecord.get("designation");

            Employee emp = new Employee();
            emp.setName(name);
            emp.setEmployeeId(employeeId);
            emp.setPassword(password);
            emp.setEmail(emailId);  // Include emailId if not already
            emp.setDesignation(designation);

            // Save to DB
            employeeRepository.save(emp);
        }
        return skipped.length() == 0
                ? "CSV uploaded successfully."
                : "CSV uploaded with warnings:\n" + skipped;
    }


}
