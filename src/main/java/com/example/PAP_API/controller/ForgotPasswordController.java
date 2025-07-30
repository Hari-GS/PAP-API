package com.example.PAP_API.controller;

import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.repository.EmployeeRepository;
import com.example.PAP_API.repository.HRManagerRepository;
import com.example.PAP_API.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class ForgotPasswordController {

    @Autowired
    private HRManagerRepository hrManagerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<HRManager> optionalUser = hrManagerRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        String token = jwtUtil.generateResetToken(optionalUser.get()); // 15min expiry

        // Example link
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password:\n" + resetLink);
        mailSender.send(message);

        return ResponseEntity.ok("Reset link sent to email.");
    }
}
