package com.example.PAP_API.services;

import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.Performance;
import com.example.PAP_API.repository.EmployeeRepository;
import com.example.PAP_API.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Performance> getAllPerformanceData() {
        return performanceRepository.findAll();
    }

}
