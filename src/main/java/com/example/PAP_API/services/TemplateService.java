package com.example.PAP_API.services;

import com.example.PAP_API.services.UserContextService;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.model.Question;
import com.example.PAP_API.model.Template;
import com.example.PAP_API.repository.HRManagerRepository;
import com.example.PAP_API.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private HRManagerRepository hrManagerRepository;

    @Autowired
    private UserContextService userContextService;

    public List<Template> getAllTemplates() {
        Long currentHRId = userContextService.getCurrentUserId();
        return templateRepository.findDefaultAndHrTemplates(currentHRId);
    }

    public Template getTemplateById(Long id) {
        Long currentHRId = userContextService.getCurrentUserId();
        return templateRepository.findAccessibleTemplate(id, currentHRId)
                .orElseThrow(() -> new RuntimeException("Template not found or access denied"));
    }

    public Template saveTemplate(Template template) {
        Long currentHRId = userContextService.getCurrentUserId();
        HRManager hrManager = hrManagerRepository.findById(currentHRId)
                .orElseThrow(() -> new RuntimeException("HRManager not found"));

        template.setHrManager(hrManager);

        if (template.getQuestions() != null) {
            for (int i = 0; i < template.getQuestions().size(); i++) {
                Question q = template.getQuestions().get(i);
                q.setTemplate(template);
                q.setOrderIndex(i);
            }
        }

        return templateRepository.save(template);
    }

    public Template updateTemplate(Long id, Template newTemplate) {
        Template existing = getTemplateById(id); // Includes ownership check

        existing.setTitle(newTemplate.getTitle());
        existing.getQuestions().clear();

        if (newTemplate.getQuestions() != null) {
            for (int i = 0; i < newTemplate.getQuestions().size(); i++) {
                Question q = newTemplate.getQuestions().get(i);
                q.setTemplate(existing);
                q.setOrderIndex(i);
                existing.getQuestions().add(q);
            }
        }

        return templateRepository.save(existing);
    }

    public void deleteTemplate(Long id) {
        Template template = getTemplateById(id); // Ownership check
        templateRepository.delete(template);
    }
}
