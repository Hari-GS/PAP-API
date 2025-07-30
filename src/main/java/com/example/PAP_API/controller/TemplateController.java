package com.example.PAP_API.controller;

import com.example.PAP_API.dto.TemplateBasicDto;
import com.example.PAP_API.dto.TemplateDTO;
import com.example.PAP_API.mappers.TemplateMapper;
import com.example.PAP_API.model.Template;
import com.example.PAP_API.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateMapper templateMapper;

    @GetMapping
    public List<TemplateDTO> getAllTemplates() {
        return templateService.getAllTemplates().stream()
                .map(templateMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TemplateDTO createTemplate(@RequestBody TemplateDTO templateDTO) {
        Template template = templateMapper.toEntity(templateDTO);
        Template saved = templateService.saveTemplate(template);
        return templateMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public TemplateDTO updateTemplate(@PathVariable Long id, @RequestBody TemplateDTO dto) {
        System.out.println("im called");
        Template updated = templateService.updateTemplate(id, templateMapper.toEntity(dto));
        return templateMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateDTO> getTemplateById(@PathVariable Long id) {
        Template template = templateService.getTemplateById(id);
        return ResponseEntity.ok(templateMapper.toDto(template));
    }

    @GetMapping("/basic")
    public List<TemplateBasicDto> getTemplateTitles() {
        return templateService.getAllTemplates().stream()
                .map(template -> {
                    TemplateBasicDto dto = new TemplateBasicDto();
                    dto.setId(template.getId());
                    dto.setTitle(template.getTitle());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
