package com.example.PAP_API.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppraisalParticipantDto {
    private Long id;
    private String employeeId;
    private String employeeName;
    private String designation;
    private String managerName;
    private String status;


    private List<AppraisalQuestionDto> questions;
}
