package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class ParticipantAppraisalAnswersDto {
    private Long id;
    private String answerText;
    private String reportingPersonComment;
    private Long questionId;
    private Long participantId;
}
