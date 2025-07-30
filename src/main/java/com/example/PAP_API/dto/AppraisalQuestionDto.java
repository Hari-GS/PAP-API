package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class AppraisalQuestionDto {
    private String text;
    private boolean showPoints;
    private int orderIndex;
}
