package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class EmployeeHierarchyDto {
    private String employeeId;
    private String name;
    private String designation;
    private String managerId;
}
