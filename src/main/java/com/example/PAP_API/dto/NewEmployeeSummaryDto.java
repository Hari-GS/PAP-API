package com.example.PAP_API.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEmployeeSummaryDto {
    private String name;
    private String designation;
    private String employeeId;
    private String manager;
}
