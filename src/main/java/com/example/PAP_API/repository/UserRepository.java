package com.example.PAP_API.repository;

import com.example.PAP_API.model.HRManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<HRManager, Long> {

    Optional<HRManager> findByEmail(String login);
}