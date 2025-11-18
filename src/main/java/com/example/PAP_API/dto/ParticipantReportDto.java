package com.example.PAP_API.dto;

import lombok.Data;
import java.util.List;

@Data
public class ParticipantReportDto {

    private Long participantId;
    private String employeeId;
    private String employeeName;
    private String designation;
    private String managerName;
    private String appraisalTitle;
    private String appraisalType;
    private String appraisalStage;

    private Double averageSelfScore;
    private Double averageManagerScore;
    private Double scoreDifference;
    private Double agreementPercentage;

    private List<ReviewAnswerDto> selfReview;
    private List<ReviewAnswerDto> reportingReview;
}
