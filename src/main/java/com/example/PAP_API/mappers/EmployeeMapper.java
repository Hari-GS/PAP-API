package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.EmployeeDto;
import com.example.PAP_API.dto.EmployeeFullDto;
import com.example.PAP_API.dto.EmployeeSummaryDto;
import com.example.PAP_API.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeSummaryDto toSummaryDTO(Employee employee);

    List<EmployeeSummaryDto> toSummaryDTOs(List<Employee> employees);

    Employee toEntity(EmployeeDto dto);
    EmployeeDto toDto(Employee employee);

    @Mapping(source = "hrManager.id", target = "hrManagerId")
    EmployeeFullDto toFullDto(Employee employee);

    @Mapping(target = "hrManager.id", source = "hrManagerId")
    Employee toFullEntity(EmployeeFullDto dto);
}
