package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SelfAppraisalAnswerMapper {

    // Map question.id -> questionId and question.text -> questionText
    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "question.text", target = "questionText")
    SelfAppraisalAnswerDto toDTO(SelfAppraisalAnswer answer);

    List<SelfAppraisalAnswerDto> toDTOList(List<SelfAppraisalAnswer> answers);
}
