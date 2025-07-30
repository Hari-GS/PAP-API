package com.example.PAP_API.controller;

import com.example.PAP_API.dto.EmployeeSummaryDto;
import com.example.PAP_API.dto.WelcomeCardDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.mappers.UserMapper;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.services.HRManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hr-managers")
public class HRManagerController {

    private final HRManagerService hrManagerService;

    private final UserMapper userMapper;

    @Autowired
    public HRManagerController(HRManagerService hrManagerService, UserMapper userMapper) {
        this.hrManagerService = hrManagerService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<WelcomeCardDto> getCurrentUser(@AuthenticationPrincipal UserDto hrManager) {
        System.out.println(hrManager.toString());
        WelcomeCardDto hrWelcomeCardDto = userMapper.toWelcomeCardDto(hrManager);
        return ResponseEntity.ok(hrWelcomeCardDto);
    }

    @GetMapping
    public ResponseEntity<List<HRManager>> getAllHRManagers() {
        return ResponseEntity.ok(hrManagerService.getAllHRManagers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HRManager> getHRManagerById(@PathVariable Long id) {
        return hrManagerService.getHRManagerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HRManager> createHRManager(@RequestBody HRManager hrManager) {
        return ResponseEntity.ok(hrManagerService.createHRManager(hrManager));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HRManager> updateHRManager(@PathVariable Long id, @RequestBody HRManager hrManager) {
        return hrManagerService.updateHRManager(id, hrManager)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHRManager(@PathVariable Long id) {
        return hrManagerService.deleteHRManager(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
