package com.example.PAP_API.services;

import com.example.PAP_API.dto.*;
import com.example.PAP_API.mappers.NewEmployeeMapper;
import com.example.PAP_API.mappers.UserMapper;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.model.Organization;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.example.PAP_API.repository.OrganizationRepository;
import com.example.PAP_API.repository.UserRepository;
import com.example.PAP_API.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final NewEmployeeMapper newEmployeeMapper;

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    private final UserContextService userContextService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private NewEmployeeRepository newEmployeeRepository;

    public UserDto login(CredentialsDto credentialsDto) {
        HRManager user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<HRManager> optionalUser = userRepository.findByEmail(userDto.getEmail());
        Optional<NewEmployee> optionalEmployee = newEmployeeRepository.findByEmail(userDto.getEmail());

        if (optionalEmployee.isPresent()) {
            throw new AppException("Email already exists as employee", HttpStatus.BAD_REQUEST);
        }

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        // Fetch organization using publicId
        Organization org = organizationRepository.findByOrganizationId(userDto.getOrganizationId())
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.BAD_REQUEST));
        if (!"VERIFIED".equals(org.getStatus())) {
            throw new AppException("Organization not verified yet", HttpStatus.BAD_REQUEST);
        }

        // Map DTO to entity
        HRManager user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
        user.setOrganization(org); // Set the relationship

        HRManager savedUser = userRepository.save(user);

        NewEmployee employee = new NewEmployee();
        employee.setEmployeeId(savedUser.getEmployeeId());
        employee.setName(savedUser.getName());
        employee.setDesignation("HR Manager");
        employee.setEmail(savedUser.getEmail());
        employee.setPassword(savedUser.getPassword());
        employee.setStatus("ACTIVE");
        employee.setHrManager(savedUser);
        employee.setOrganization(savedUser.getOrganization());
        newEmployeeRepository.save(employee);

        // --- Send Welcome Email ---
        String htmlContent = emailTemplateService.getWelcomeEmail(savedUser.getName());
        emailService.sendHtmlMail(savedUser.getEmail(), "Welcome to Performance Appraisal Platform", htmlContent);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByLogin(String login) {
        Optional<HRManager> hrOpt = userRepository.findByEmail(login);
        if (hrOpt.isPresent()) {
            UserDto userDto = userMapper.toUserDto(hrOpt.get());
            userDto.setDesignation("Human Resources Manager");
            return userDto;
        }

        Optional<NewEmployee> empOpt = newEmployeeRepository.findByEmail(login);
        if (empOpt.isPresent()) {
            return userMapper.toUserDto(empOpt.get());
        }

        throw new AppException("Unknown user", HttpStatus.NOT_FOUND);
    }


    public NewEmployeeDto loginEmployee(CredentialsDto dto) {
        NewEmployee employee = newEmployeeRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AppException("Unknown employee", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(dto.getPassword()), employee.getPassword())) {
            return newEmployeeMapper.toDTO(employee);
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public Object loginUnified(CredentialsDto credentials) {
        Optional<HRManager> hrUser = userRepository.findByEmail(credentials.getEmail());
        Optional<NewEmployee> employee = newEmployeeRepository.findByEmail(credentials.getEmail());

        if (hrUser.isPresent()) {
            if (passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), hrUser.get().getPassword())) {
                UserDto dto = userMapper.toUserDto(hrUser.get()); // map user
                dto.setRole("hr");
                return dto;
            } else {
                throw new AppException("Invalid password", HttpStatus.UNAUTHORIZED);
            }
        }

        if (employee.isPresent()) {
            if (passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), employee.get().getPassword())) {
                NewEmployeeDto dto = newEmployeeMapper.toDTO(employee.get()); // map employee
                if(employee.get().getIsDirector()){
                    dto.setRole("director");
                    return dto;
                }
                dto.setRole("employee");
                return dto;
            } else {
                throw new AppException("Invalid password", HttpStatus.UNAUTHORIZED);
            }
        }

        throw new AppException("User not found", HttpStatus.NOT_FOUND);
    }

    public Long resolveHrIdForUser(){
        UserDto user = userContextService.getCurrentUser();
        if(user.getRole().equals("director")){
            return newEmployeeRepository.findById(user.getId()).get().getHrManager().getId();
        }else{
            return userContextService.getCurrentUserId();
        }
    }

}