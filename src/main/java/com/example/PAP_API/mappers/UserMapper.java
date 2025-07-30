package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.WelcomeCardDto;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.dto.SignUpDto;
import com.example.PAP_API.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "organization.id", target = "organization")  // Custom mapping
    })
    UserDto toUserDto(HRManager user);

    HRManager signUpToUser(SignUpDto signUpDto);

    WelcomeCardDto toWelcomeCardDto(UserDto hrManager);
}