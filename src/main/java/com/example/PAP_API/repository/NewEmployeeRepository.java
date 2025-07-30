package com.example.PAP_API.repository;

import com.example.PAP_API.model.Employee;
import com.example.PAP_API.model.NewEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewEmployeeRepository extends JpaRepository<NewEmployee, Long> {
    // You get basic CRUD methods automatically
    List<NewEmployee> findByHrManagerId(Long id);

    Optional<NewEmployee> findByEmployeeId(String id);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    void deleteByEmployeeId(String employeeId);

    Optional<NewEmployee> findByEmail(String email);

    Optional<NewEmployee> findByEmployeeIdAndHrManagerId(String employeeId, Long hrManagerId);

    Optional<NewEmployee> findByEmailAndHrManagerId(String employeeId, Long hrManagerId);

    boolean existsByEmployeeIdAndHrManagerId(String employeeId, Long hrManagerId);

    boolean existsByEmailAndHrManagerId(String email, Long hrId);

    Optional<NewEmployee> findByEmailAndEmployeeId(String email, String employeeId);
}
