package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class AppraisalParticipantSummaryDto {
    private Long id;
    private String employeeId;
    private String employeeName;
    private String designation;
}
