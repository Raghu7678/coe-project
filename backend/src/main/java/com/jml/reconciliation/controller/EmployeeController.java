package com.jml.reconciliation.controller;

import com.jml.reconciliation.entity.Employee;
import com.jml.reconciliation.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        if (employee.getLastUpdated() == null) {
            employee.setLastUpdated(LocalDateTime.now());
        }
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @Valid @RequestBody Employee updated) {
        return employeeRepository.findById(id).map(emp -> {
            emp.setName(updated.getName());
            emp.setEmail(updated.getEmail());
            emp.setDepartment(updated.getDepartment());
            emp.setPreviousRole(emp.getCurrentRole());
            emp.setCurrentRole(updated.getCurrentRole());
            emp.setEmploymentStatus(updated.getEmploymentStatus());
            emp.setLastRoleChangeDate(LocalDateTime.now());
            emp.setLastUpdated(LocalDateTime.now());
            return ResponseEntity.ok(employeeRepository.save(emp));
        }).orElse(ResponseEntity.notFound().build());
    }
}
