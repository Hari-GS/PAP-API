package com.example.PAP_API.controller;

import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.repository.HRManagerRepository;
import com.example.PAP_API.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class ResetPasswordController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HRManagerRepository hrManagerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        try {
            String email = jwtUtil.extractEmail(token);
            Optional<HRManager> optionalUser = hrManagerRepository.findByEmail(email);

            if (optionalUser.isPresent()) {
                HRManager hrManager = optionalUser.get();
                hrManager.setPassword(passwordEncoder.encode(newPassword));
                hrManagerRepository.save(hrManager);
                return ResponseEntity.ok("Password reset successful.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }
}
