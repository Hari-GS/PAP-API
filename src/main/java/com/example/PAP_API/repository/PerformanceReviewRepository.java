package com.example.PAP_API.repository;

import com.example.PAP_API.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    long countByCompletedTrue();
    long countByCompletedFalse();

    @Query("SELECT AVG(r.score) FROM PerformanceReview r WHERE r.completed = true")
    Double findAverageScore();
}
