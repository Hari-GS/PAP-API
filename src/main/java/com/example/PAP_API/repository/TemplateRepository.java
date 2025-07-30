package com.example.PAP_API.repository;

import com.example.PAP_API.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByHrManager_Id(Long hrId);
    Optional<Template> findByIdAndHrManager_Id(Long id, Long hrId);
}
