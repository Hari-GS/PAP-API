package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.EmployeeSummaryDto;
import com.example.PAP_API.dto.NewEmployeeDto;
import com.example.PAP_API.dto.NewEmployeeSummaryDto;
import com.example.PAP_API.model.NewEmployee;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewEmployeeMapper {

    @Mapping(source = "manager.employeeId", target = "managerId")
    NewEmployeeDto toDTO(NewEmployee employee);

    @Mapping(target = "manager", ignore = true)  // Ignore mapping manager in the automatic mapping
    NewEmployee toEntity(NewEmployeeDto employeeDTO);

    @Mapping(source = "manager.employeeId", target = "manager")
    NewEmployeeSummaryDto toSummaryDTO(NewEmployee employee);

    @Mapping(source = "manager.employeeId", target = "manager")
    List<NewEmployeeSummaryDto> toSummaryDTOs(List<NewEmployee> employees);

    void updateEmployeeFromDto(NewEmployeeDto dto, @MappingTarget NewEmployee entity);
}
