package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SelfAppraisalAnswerMapper {

    // Map nested `question.id` to `questionId` in DTO
    @Mapping(source = "question.id", target = "questionId")
    SelfAppraisalAnswerDto toDTO(SelfAppraisalAnswer answer);

    List<SelfAppraisalAnswerDto> toDTOList(List<SelfAppraisalAnswer> answers);
}
