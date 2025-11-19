package com.example.PAP_API.repository;

import com.example.PAP_API.enums.Stage;
import com.example.PAP_API.model.Appraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    List<Appraisal> findByHrManagerId(Long hrManagerId);
    Optional<Appraisal> findByIdAndHrManagerId(Long id, Long hrId);
    List<Appraisal> findByHrManagerIdAndStage(Long hrManagerId, Stage stage);

    Optional<Appraisal> findTopByHrManagerIdOrderByEndDateDesc(Long hrId);
    Optional<Appraisal> findTopByHrManagerIdOrderByCreatedAtDescIdDesc(Long hrId);


    // ✅ New method: Get all appraisals except CLOSED
    List<Appraisal> findByHrManagerIdAndStageNot(Long hrManagerId, Stage stage);

    Optional<Appraisal> findTopByHrManagerIdOrderByIdDesc(Long hrId);

}
