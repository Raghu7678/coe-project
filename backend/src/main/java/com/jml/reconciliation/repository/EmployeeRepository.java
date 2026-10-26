package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.Employee;
import com.jml.reconciliation.model.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for {@link Employee} entities.
 * 
 * Provides automated query derivation for identity lookups by unique username,
 * unique employee ID, and filtering by employment status (ACTIVE, LEFT, ROLE_MOVER).
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Looks up an employee identity by unique username (e.g. 'usr_alex').
     * @param username unique user account handle
     * @return Optional containing matching Employee if found
     */
    Optional<Employee> findByUsername(String username);

    /**
     * Looks up an employee identity by unique employee ID (e.g. 'EMP-1001').
     * @param employeeId corporate employee code
     * @return Optional containing matching Employee if found
     */
    Optional<Employee> findByEmployeeId(String employeeId);

    /**
     * Finds all employee records matching a specific employment status.
     * @param status EmploymentStatus enum (ACTIVE, LEFT, ROLE_MOVER, etc.)
     * @return list of matching Employee records
     */
    List<Employee> findByStatus(EmploymentStatus status);
}
