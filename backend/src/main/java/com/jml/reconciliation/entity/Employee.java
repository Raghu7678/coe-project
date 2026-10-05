package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.EmploymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String fullName;

    private String email;

    private String department;

    private String currentRole;

    private String previousRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status;

    private LocalDateTime hiredAt;

    private LocalDateTime lastRoleChangeAt;

    public Employee() {}

    public Employee(String employeeId, String username, String fullName, String email, String department, String currentRole, String previousRole, EmploymentStatus status, LocalDateTime hiredAt, LocalDateTime lastRoleChangeAt) {
        this.employeeId = employeeId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.currentRole = currentRole;
        this.previousRole = previousRole;
        this.status = status;
        this.hiredAt = hiredAt;
        this.lastRoleChangeAt = lastRoleChangeAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    public String getPreviousRole() { return previousRole; }
    public void setPreviousRole(String previousRole) { this.previousRole = previousRole; }

    public EmploymentStatus getStatus() { return status; }
    public void setStatus(EmploymentStatus status) { this.status = status; }

    public LocalDateTime getHiredAt() { return hiredAt; }
    public void setHiredAt(LocalDateTime hiredAt) { this.hiredAt = hiredAt; }

    public LocalDateTime getLastRoleChangeAt() { return lastRoleChangeAt; }
    public void setLastRoleChangeAt(LocalDateTime lastRoleChangeAt) { this.lastRoleChangeAt = lastRoleChangeAt; }
}
