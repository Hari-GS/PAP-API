package com.example.PAP_API.services;

import com.example.PAP_API.dto.DashboardSummaryDto;
import com.example.PAP_API.repository.EmployeeRepository;
import com.example.PAP_API.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewRepository reviewRepository;

    public DashboardSummaryDto getSummary() {
        int totalEmployees = (int) employeeRepository.count();
        int completed = (int) reviewRepository.countByCompletedTrue();
        int pending = (int) reviewRepository.countByCompletedFalse();
        Double avgScore = reviewRepository.findAverageScore();

        return new DashboardSummaryDto(
                totalEmployees,
                completed,
                pending,
                avgScore != null ? avgScore : 0.0
        );
    }
}
