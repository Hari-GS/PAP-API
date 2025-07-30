package com.example.PAP_API.services;

import com.example.PAP_API.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDto) {
            return (UserDto) authentication.getPrincipal();
        }
        throw new IllegalStateException("Principal is not UserDto");
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public Long getCurrentOrganizationId() {
        return getCurrentUser().getOrganization();
    }
}
