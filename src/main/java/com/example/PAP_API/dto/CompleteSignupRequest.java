package com.example.PAP_API.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CompleteSignupRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String password;
}
