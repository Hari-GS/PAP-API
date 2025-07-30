package com.example.PAP_API.dto;

import lombok.Data;

@Data
public class SetPasswordDto {
    private String employeeId;
    private String email;
    private String newPassword;
}