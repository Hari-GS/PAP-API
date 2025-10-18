package com.example.PAP_API.controller;

import com.example.PAP_API.dto.DashboardSummaryDto;
import com.example.PAP_API.services.DashboardService;
import com.example.PAP_API.services.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    UserContextService userContextService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        DashboardSummaryDto summary = dashboardService.getDashboardSummary(userContextService.getCurrentUserId());
        return ResponseEntity.ok(summary);
    }
}
