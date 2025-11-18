package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class SelfAppraisalAnswerDto {
    private Long id;
    private Long questionId;
    private String questionText;
    private Integer answerScore;
    private String answerText;
    private Integer reportingPersonScore;
    private String reportingPersonComment;
}
