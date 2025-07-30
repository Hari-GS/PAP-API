package com.example.PAP_API.dto;

import com.example.PAP_API.enums.Stage;
import lombok.Data;

import java.util.List;

@Data
public class AppraisalDto {

    private Long id;
    private String title;
    private String type;
    private String startDate;
    private String selfAppraisalEndDate;
    private String endDate;
    private String description;
    private String createdAt;
    private Stage stage= Stage.CREATED;

    private List<AppraisalParticipantDto> participants;
}
