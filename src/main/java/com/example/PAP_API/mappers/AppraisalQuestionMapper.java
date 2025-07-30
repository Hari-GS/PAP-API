package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.AppraisalQuestionDto;
import com.example.PAP_API.model.AppraisalQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppraisalQuestionMapper {
    AppraisalQuestionMapper INSTANCE = Mappers.getMapper(AppraisalQuestionMapper.class);

    AppraisalQuestionDto toDto(AppraisalQuestion entity);
    AppraisalQuestion toEntity(AppraisalQuestionDto dto);
}
