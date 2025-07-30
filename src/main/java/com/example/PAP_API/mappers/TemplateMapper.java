package com.example.PAP_API.mappers;

import com.example.PAP_API.dto.TemplateDTO;
import com.example.PAP_API.model.Template;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    TemplateMapper INSTANCE = Mappers.getMapper(TemplateMapper.class);

    TemplateDTO toDto(Template template);
    Template toEntity(TemplateDTO dto);
}
