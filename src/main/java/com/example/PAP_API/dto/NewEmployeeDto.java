package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class NewEmployeeDto {
    private String employeeId;
    private String name;
    private String designation;
    private String managerId;  // only ID to avoid recursion
    private String email;
    private String token;
    private String role;
}
