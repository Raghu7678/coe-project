package com.jml.reconciliation.entity;

import com.jml.reconciliation.model.enums.EmploymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(name = "current_role", nullable = false, length = 50)
    private String currentRole;

    @Column(name = "previous_role", length = 50)
    private String previousRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmploymentStatus employmentStatus;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "last_role_change_date")
    private LocalDateTime lastRoleChangeDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Employee() {}

    public Employee(String employeeId, String userId, String name, String email, String department, 
                    String currentRole, String previousRole, EmploymentStatus employmentStatus, 
                    LocalDateTime joinDate, LocalDateTime lastRoleChangeDate, LocalDateTime lastUpdated) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.currentRole = currentRole;
        this.previousRole = previousRole;
        this.employmentStatus = employmentStatus;
        this.joinDate = joinDate;
        this.lastRoleChangeDate = lastRoleChangeDate;
        this.lastUpdated = lastUpdated;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    public String getPreviousRole() { return previousRole; }
    public void setPreviousRole(String previousRole) { this.previousRole = previousRole; }

    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }

    public LocalDateTime getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }

    public LocalDateTime getLastRoleChangeDate() { return lastRoleChangeDate; }
    public void setLastRoleChangeDate(LocalDateTime lastRoleChangeDate) { this.lastRoleChangeDate = lastRoleChangeDate; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
