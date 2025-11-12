package com.example.PAP_API.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.PAP_API.config.CurrentUser;
import com.example.PAP_API.config.UserAuthenticationProvider;
import com.example.PAP_API.dto.*;
import com.example.PAP_API.exception.AppException;
import com.example.PAP_API.mappers.NewEmployeeMapper;
import com.example.PAP_API.mappers.UserMapper;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.example.PAP_API.services.NewEmployeeService;
import com.example.PAP_API.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final NewEmployeeRepository newEmployeeRepository;
    private final PasswordEncoder passwordEncoder;

    private final NewEmployeeService newEmployeeService;

    private final NewEmployeeMapper newEmployeeMapper;

    @Autowired
    UserMapper userMapper;

    @PostMapping("/hr/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail(),"hr"));
        userDto.setRole("hr");
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/hr/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = userService.register(user);
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser.getToken(),"hr"));
        createdUser.setRole("hr");
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/employee/login")
    public ResponseEntity<NewEmployeeDto> loginEmployee(@RequestBody CredentialsDto dto) {
        NewEmployeeDto user = userService.loginEmployee(dto);
        user.setToken(userAuthenticationProvider.createToken(user.getEmail(),"employee"));
        user.setRole("employee");
        return ResponseEntity.ok(user);
    }

    @PostMapping("/employee/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordDto dto) {
        NewEmployee employee = newEmployeeRepository.findByEmailAndEmployeeId(dto.getEmail(), dto.getEmployeeId())
                .orElseThrow(() -> new AppException("Employee not found", HttpStatus.NOT_FOUND));

        if (employee.getPassword() != null) {
            //throw new AppException("Password already set. Please login.", HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok("Password already set. Please login to change password");
        }

        employee.setPassword(passwordEncoder.encode(CharBuffer.wrap(dto.getNewPassword())));
        newEmployeeRepository.save(employee);
        return ResponseEntity.ok("Password set successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUnified(@RequestBody @Valid CredentialsDto credentialsDto) {
        Object userDto = userService.loginUnified(credentialsDto);
        System.out.println(credentialsDto);
        String email;
        String role;

        if (userDto instanceof UserDto) {
            UserDto user = (UserDto) userDto;
            role = user.getRole();
            email = user.getEmail();
            user.setToken(userAuthenticationProvider.createToken(email, role));
            return ResponseEntity.ok(user);
        } else if (userDto instanceof NewEmployeeDto) {
            NewEmployeeDto emp = (NewEmployeeDto) userDto;
            role = emp.getRole();
            email = emp.getEmail();
            emp.setToken(userAuthenticationProvider.createToken(email, role));
            return ResponseEntity.ok(emp);
        }

        throw new AppException("Unexpected login result", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDto user) {
        return new ResponseEntity<UserDto>(user, HttpStatus.OK);
    }

    @GetMapping("/verify-invite")
    public ResponseEntity<?> verifyInviteToken(@RequestParam("token") String token) {
        try {
            // 1️⃣ Verify JWT
            DecodedJWT decoded = userAuthenticationProvider.validateInviteToken(token);
            String email = decoded.getSubject();
            Long hrId = decoded.getClaim("hrId").asLong();

            // 2️⃣ Fetch employee
            NewEmployee newEmployee = newEmployeeRepository
                    .findByEmailAndHrManagerId(email, hrId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // 3️⃣ Map to DTO
            NewEmployeeDto dto = newEmployeeMapper.toDTO(newEmployee);
            dto.setReportingPerson(newEmployee.getManager() == null ? null : newEmployee.getManager().getName());

            return ResponseEntity.ok(dto);

        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            return ResponseEntity.status(401).body("Token expired");
        } catch (com.auth0.jwt.exceptions.JWTVerificationException e) {
            return ResponseEntity.status(400).body("Invalid token");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/complete-signup")
    public ResponseEntity<?> completeSignup(@RequestBody CompleteSignupRequest request) {
        String token = request.getToken();
        String password = request.getPassword();

        if (token == null || password == null) {
            return ResponseEntity.badRequest().body("Token and password are required");
        }

        try {
            newEmployeeService.completeSignup(token, password);
            return ResponseEntity.ok("Signup completed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}