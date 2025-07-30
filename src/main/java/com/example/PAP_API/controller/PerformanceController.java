package com.example.PAP_API.controller;

import com.example.PAP_API.model.Performance;
import com.example.PAP_API.services.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/performance")
    public List<Performance> getPerformanceData() {
        return performanceService.getAllPerformanceData();
    }

}
