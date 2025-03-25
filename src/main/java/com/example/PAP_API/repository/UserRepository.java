package com.example.PAP_API.repository;

import com.example.PAP_API.model.TheUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<TheUser, Long> {

    Optional<TheUser> findByLogin(String login);
}