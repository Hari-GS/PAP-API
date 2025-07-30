package com.example.PAP_API.repository;

import com.example.PAP_API.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {
}
