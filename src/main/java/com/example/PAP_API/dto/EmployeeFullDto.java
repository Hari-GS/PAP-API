package com.example.PAP_API.dto;

import lombok.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFullDto {
    private Long id;
    private String name;
    private String email;
    private String employeeId;
    private String designation;

    private Long hrManagerId; // Just store the HRManager ID

    private Date dateOfBirth;
    private Date dateOfJoining;
    private String gender;
    private String address;
    private String mobileNumber;
}
