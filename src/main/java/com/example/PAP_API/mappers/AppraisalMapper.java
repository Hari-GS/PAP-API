package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.AppraisalDto;
import com.example.PAP_API.dto.NewEmployeeDto;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.NewEmployee;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.example.PAP_API.services.NewEmployeeService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppraisalParticipantMapper.class})
public abstract class AppraisalMapper {

    @Autowired
    protected NewEmployeeService employeeService;

    @Autowired
    protected NewEmployeeRepository newEmployeeRepository;

    public abstract AppraisalDto toDto(Appraisal appraisal);
    public abstract Appraisal toEntity(AppraisalDto dto);
    public abstract List<AppraisalDto> toDtoList(List<Appraisal> appraisals);

    @AfterMapping
    protected void enrichParticipants(@MappingTarget Appraisal appraisal) {
        if (appraisal.getParticipants() != null) {
            for (AppraisalParticipant participant : appraisal.getParticipants()) {
                ResponseEntity<NewEmployeeDto> response = employeeService.getEmployeeById(participant.getEmployeeId());

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    NewEmployeeDto employee = response.getBody();

                    participant.setDesignation(employee.getDesignation());
                    participant.setManagerName(
                            employee.getManagerId() != null ? employeeService.getEmployeeById(employee.getManagerId()).getBody().getName() : "N/A"
                    );
                    participant.setReportingPerson(employee.getManagerId()!= null ? newEmployeeRepository.findByEmployeeId(employee.getManagerId()).get() : null);
                    participant.setAppraisal(appraisal); // maintain back-reference
                } else {
                    throw new RuntimeException("Employee not found or service error for ID: " + participant.getEmployeeId());
                }
            }
        }
    }

}
