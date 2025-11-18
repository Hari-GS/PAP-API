package com.example.PAP_API.repository;

import com.example.PAP_API.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByHrManager_Id(Long hrId);
    Optional<Template> findByIdAndHrManager_Id(Long id, Long hrId);

    @Query("SELECT t FROM Template t WHERE t.isDefault = true OR t.hrManager.id = :hrId")
    List<Template> findDefaultAndHrTemplates(@Param("hrId") Long hrId);

    @Query("""
    SELECT t FROM Template t
    WHERE t.id = :id
      AND (t.hrManager.id = :hrId OR t.hrManager IS NULL)
""")
    Optional<Template> findAccessibleTemplate(@Param("id") Long id, @Param("hrId") Long hrId);


}
