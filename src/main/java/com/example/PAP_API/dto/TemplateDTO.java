package com.example.PAP_API.dto;

import lombok.Data;
import java.util.List;

@Data
public class TemplateDTO {
    private Long id;
    private String title;
    private List<QuestionDTO> questions;
    private Boolean isDefault;
}
