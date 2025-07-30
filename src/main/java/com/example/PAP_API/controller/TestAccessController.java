package com.example.PAP_API.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestAccessController {

    // 👨‍💼 HR-only access
    @GetMapping("/hr")
    public ResponseEntity<String> hrAccess() {
        return ResponseEntity.ok("✅ Hello HR! You have access to this HR-only endpoint.");
    }

    // 👷‍♂️ Employee-only access
    @GetMapping("/employee")
    public ResponseEntity<String> employeeAccess() {
        return ResponseEntity.ok("✅ Hello Employee! You have access to this Employee-only endpoint.");
    }
}
