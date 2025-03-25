package com.example.PAP_API.mappers;

import com.example.PAP_API.model.TheUser;
import com.example.PAP_API.dto.SignUpDto;
import com.example.PAP_API.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(TheUser user);

    @Mapping(target = "password", ignore = true)
    TheUser signUpToUser(SignUpDto signUpDto);

}