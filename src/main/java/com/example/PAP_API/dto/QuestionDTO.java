package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private boolean showPoints;
}
