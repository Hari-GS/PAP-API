package com.example.PAP_API.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUser {
    private Long id;
    private String email;
    private String role;
    private Long organizationId;
}
