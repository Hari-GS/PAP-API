package com.example.PAP_API.repository;

import com.example.PAP_API.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    boolean existsByEmail(String emailId);

    boolean existsByEmployeeId(String employeeId);

    List<Employee> findByHrManagerId(Long id);
}
