package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.AppraisalParticipantDto;
import com.example.PAP_API.dto.AppraisalParticipantSummaryDto;
import com.example.PAP_API.model.AppraisalParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppraisalQuestionMapper.class})
public interface AppraisalParticipantMapper {
    AppraisalParticipantMapper INSTANCE = Mappers.getMapper(AppraisalParticipantMapper.class);

    AppraisalParticipantDto toDto(AppraisalParticipant entity);
    AppraisalParticipant toEntity(AppraisalParticipantDto dto);

    List<AppraisalParticipantDto> toDto(List<AppraisalParticipant> entity);

    List<AppraisalParticipantSummaryDto> toSummaryDto(List<AppraisalParticipant> entity);
}
