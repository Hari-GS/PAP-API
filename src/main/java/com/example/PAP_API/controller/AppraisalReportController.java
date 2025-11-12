package com.example.PAP_API.controller;

import com.example.PAP_API.dto.AppraisalDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.mappers.AppraisalMapper;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.services.AppraisalReportService;
import com.example.PAP_API.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class AppraisalReportController {

    private final AppraisalReportService appraisalReportService;

    @Autowired
    public AppraisalMapper appraisalMapper;

    @Autowired
    private UserService userService;

    public AppraisalReportController(AppraisalReportService appraisalReportService) {
        this.appraisalReportService = appraisalReportService;
    }

    @GetMapping("/closed-appraisals")
    public List<AppraisalDto> getClosedAppraisals() {
        Long hrId = userService.resolveHrIdForUser();
        return appraisalMapper.toDtoList(appraisalReportService.getClosedAppraisalsByHr(hrId));
    }
}
