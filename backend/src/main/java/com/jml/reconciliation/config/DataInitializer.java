package com.jml.reconciliation.config;

import com.jml.reconciliation.entity.*;

import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import com.jml.reconciliation.service.ReconciliationEngineService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final EntitlementRepository entitlementRepository;
    private final DirectoryGroupRepository directoryGroupRepository;
    private final ApprovalRecordRepository approvalRepository;
    private final RolePolicyRepository rolePolicyRepository;
    private final DataSourceHealthRepository healthRepository;
    private final ReconciliationEngineService reconciliationEngineService;

    public DataInitializer(
            EmployeeRepository employeeRepository,
            EntitlementRepository entitlementRepository,
            DirectoryGroupRepository directoryGroupRepository,
            ApprovalRecordRepository approvalRepository,
            RolePolicyRepository rolePolicyRepository,
            DataSourceHealthRepository healthRepository,
            ReconciliationEngineService reconciliationEngineService) {
        this.employeeRepository = employeeRepository;
        this.entitlementRepository = entitlementRepository;
        this.directoryGroupRepository = directoryGroupRepository;
        this.approvalRepository = approvalRepository;
        this.rolePolicyRepository = rolePolicyRepository;
        this.healthRepository = healthRepository;
        this.reconciliationEngineService = reconciliationEngineService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() > 0) return;

        // 1. Data Feeds Health Initialization
        healthRepository.save(new DataSourceHealth("HR", HealthState.AVAILABLE, 25, 0.5));
        healthRepository.save(new DataSourceHealth("DIRECTORY", HealthState.AVAILABLE, 30, 1.0));
        healthRepository.save(new DataSourceHealth("APPLICATION_ENTITLEMENTS", HealthState.AVAILABLE, 20, 0.2));
        healthRepository.save(new DataSourceHealth("APPROVAL_HISTORY", HealthState.AVAILABLE, 15, 0.1));

        // 2. Seed Role Policies
        rolePolicyRepository.save(new RolePolicy("Developer", "GitHub", PermissionLevel.WRITE, true));
        rolePolicyRepository.save(new RolePolicy("Developer", "Jira", PermissionLevel.READ, true));
        rolePolicyRepository.save(new RolePolicy("HR Manager", "HR System", PermissionLevel.ADMIN, true));
        rolePolicyRepository.save(new RolePolicy("HR Manager", "Jira", PermissionLevel.READ, true));
        rolePolicyRepository.save(new RolePolicy("Finance Analyst", "Finance System", PermissionLevel.READ, true));
        rolePolicyRepository.save(new RolePolicy("Finance Analyst", "Jira", PermissionLevel.READ, true));
        rolePolicyRepository.save(new RolePolicy("System Administrator", "AWS Console", PermissionLevel.ADMIN, true));
        rolePolicyRepository.save(new RolePolicy("System Administrator", "GitHub", PermissionLevel.ADMIN, true));

        // 3. Seed Synthetic Users & Ground Truth Anomalies
        LocalDateTime now = LocalDateTime.now();

        // Synthetic Test Case 1: Mover (Developer -> HR Manager) with leftover GitHub ADMIN
        createEmployeeWithAccess(
                "EMP-1001", "usr_alex", "Alex Mercer", "alex.mercer@saas.com", "HR",
                "HR Manager", "Developer", EmploymentStatus.ROLE_MOVER,
                now.minusYears(2), now.minusDays(10),
                Map.of("HR System", PermissionLevel.ADMIN, "GitHub", PermissionLevel.ADMIN, "Jira", PermissionLevel.READ),
                List.of("engineering-dev", "hr-managers"),
                List.of(new ApprovalRecord("APP-101", "usr_alex", "HR System", PermissionLevel.ADMIN, "usr_alex", "sec_admin", now.minusDays(10), "APPROVED", "Role transition approval"))
        );

        // Synthetic Test Case 2: Orphaned Leaver
        createEmployeeWithAccess(
                "EMP-1002", "usr_sarah", "Sarah Jenkins", "sarah.jenkins@saas.com", "Finance",
                "Finance Analyst", "Finance Analyst", EmploymentStatus.LEFT,
                now.minusYears(3), now.minusDays(15),
                Map.of("Finance System", PermissionLevel.WRITE, "Jira", PermissionLevel.READ),
                List.of("finance-team"),
                List.of(new ApprovalRecord("APP-102", "usr_sarah", "Finance System", PermissionLevel.WRITE, "usr_sarah", "fin_lead", now.minusYears(1), "APPROVED", "Finance onboard"))
        );

        // Synthetic Test Case 3: Unapproved Privileged Access
        createEmployeeWithAccess(
                "EMP-1003", "usr_david", "David Vance", "david.vance@saas.com", "Engineering",
                "Developer", "Developer", EmploymentStatus.ACTIVE,
                now.minusYears(1), now.minusMonths(6),
                Map.of("AWS Console", PermissionLevel.ADMIN, "GitHub", PermissionLevel.WRITE, "Jira", PermissionLevel.READ),
                List.of("engineering-dev"),
                List.of()
        );

        // Synthetic Test Case 4: Critical Leaver (AWS ADMIN)
        createEmployeeWithAccess(
                "EMP-1004", "usr_marcus", "Marcus Brody", "marcus.brody@saas.com", "IT",
                "System Administrator", "System Administrator", EmploymentStatus.LEFT,
                now.minusYears(4), now.minusDays(2),
                Map.of("AWS Console", PermissionLevel.ADMIN, "GitHub", PermissionLevel.ADMIN),
                List.of("sysadmins"),
                List.of(new ApprovalRecord("APP-104", "usr_marcus", "AWS Console", PermissionLevel.ADMIN, "usr_marcus", "cto", now.minusYears(3), "APPROVED", "SysAdmin access"))
        );

        // Seed 36 Additional Synthetic Active Employees
        for (int i = 5; i <= 40; i++) {
            String empId = String.format("EMP-%04d", 1000 + i);
            String uname = "usr_emp" + i;
            String name = "User " + i;
            String dept = (i % 4 == 0) ? "Engineering" : ((i % 4 == 1) ? "HR" : ((i % 4 == 2) ? "Finance" : "IT"));
            String role = (i % 4 == 0) ? "Developer" : ((i % 4 == 1) ? "HR Manager" : ((i % 4 == 2) ? "Finance Analyst" : "System Administrator"));
            EmploymentStatus status = (i % 8 == 0) ? EmploymentStatus.LEFT : ((i % 10 == 0) ? EmploymentStatus.ROLE_MOVER : EmploymentStatus.ACTIVE);

            String mainApp = (i % 4 == 0) ? "GitHub" : ((i % 4 == 1) ? "HR System" : ((i % 4 == 2) ? "Finance System" : "AWS Console"));
            PermissionLevel perm = (role.equals("System Administrator") || role.equals("HR Manager")) ? PermissionLevel.ADMIN : PermissionLevel.WRITE;

            createEmployeeWithAccess(
                    empId, uname, name, uname + "@saas.com", dept,
                    role, role, status,
                    now.minusMonths(i), now.minusDays(i),
                    Map.of(mainApp, perm, "Jira", PermissionLevel.READ),
                    List.of(dept.toLowerCase() + "-group"),
                    List.of(new ApprovalRecord("APP-" + i, uname, mainApp, perm, uname, "manager", now.minusMonths(i), "APPROVED", "Standard onboarding"))
            );
        }

        // Initial Reconciliation Run
        reconciliationEngineService.runReconciliation();
    }

    private void createEmployeeWithAccess(
            String empId, String username, String fullName, String email, String department,
            String currentRole, String previousRole, EmploymentStatus status,
            LocalDateTime hiredAt, LocalDateTime roleChangeAt,
            Map<String, PermissionLevel> entitlements,
            List<String> directoryGroups,
            List<ApprovalRecord> approvals) {

        Employee emp = new Employee(empId, username, fullName, email, department, currentRole, previousRole, status, hiredAt, roleChangeAt);
        employeeRepository.save(emp);

        for (Map.Entry<String, PermissionLevel> entry : entitlements.entrySet()) {
            entitlementRepository.save(new Entitlement(username, entry.getKey(), entry.getValue()));
        }

        for (String groupName : directoryGroups) {
            directoryGroupRepository.save(new DirectoryGroup(groupName, username));
        }

        for (ApprovalRecord app : approvals) {
            approvalRepository.save(app);
        }
    }
}
