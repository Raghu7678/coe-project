package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.Employee;
import com.jml.reconciliation.model.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);
    List<Employee> findByStatus(EmploymentStatus status);
}
