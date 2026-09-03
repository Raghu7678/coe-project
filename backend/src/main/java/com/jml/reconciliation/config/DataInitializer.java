package com.jml.reconciliation.config;

import com.jml.reconciliation.entity.*;
import com.jml.reconciliation.model.enums.*;
import com.jml.reconciliation.repository.*;
import com.jml.reconciliation.service.BaselineEngineService;
import com.jml.reconciliation.service.ReconciliationEngineService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final RolePolicyRepository rolePolicyRepository;
    private final DirectoryGroupRepository directoryGroupRepository;
    private final EntitlementRepository entitlementRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final DataSourceHealthRepository healthRepository;
    private final ReconciliationEngineService reconciliationEngineService;
    private final BaselineEngineService baselineEngineService;

    public DataInitializer(EmployeeRepository employeeRepository,
                           RolePolicyRepository rolePolicyRepository,
                           DirectoryGroupRepository directoryGroupRepository,
                           EntitlementRepository entitlementRepository,
                           ApprovalRecordRepository approvalRecordRepository,
                           DataSourceHealthRepository healthRepository,
                           ReconciliationEngineService reconciliationEngineService,
                           BaselineEngineService baselineEngineService) {
        this.employeeRepository = employeeRepository;
        this.rolePolicyRepository = rolePolicyRepository;
        this.directoryGroupRepository = directoryGroupRepository;
        this.entitlementRepository = entitlementRepository;
        this.approvalRecordRepository = approvalRecordRepository;
        this.healthRepository = healthRepository;
        this.reconciliationEngineService = reconciliationEngineService;
        this.baselineEngineService = baselineEngineService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (employeeRepository.count() > 0) {
            return;
        }

        seedDataSourcesHealth();
        seedRolePolicies();
        seedEmployeesAndEntitlements();

        // Run initial reconciliation for baseline & prototype so dashboard has rich data out of the box!
        baselineEngineService.runBaselineReconciliation();
        reconciliationEngineService.runReconciliation();
    }

    private void seedDataSourcesHealth() {
        healthRepository.save(new DataSourceHealth("HR", HealthState.AVAILABLE, LocalDateTime.now(), "Fresh", 40));
        healthRepository.save(new DataSourceHealth("DIRECTORY", HealthState.AVAILABLE, LocalDateTime.now(), "Fresh", 40));
        healthRepository.save(new DataSourceHealth("APPLICATION_ENTITLEMENTS", HealthState.AVAILABLE, LocalDateTime.now(), "Fresh", 80));
        healthRepository.save(new DataSourceHealth("APPROVAL_HISTORY", HealthState.AVAILABLE, LocalDateTime.now(), "Fresh", 50));
    }

    private void seedRolePolicies() {
        // Developer
        rolePolicyRepository.save(new RolePolicy("Developer", "GitHub", PermissionLevel.WRITE, true));
        rolePolicyRepository.save(new RolePolicy("Developer", "Jira", PermissionLevel.USER, true));

        // HR Manager
        rolePolicyRepository.save(new RolePolicy("HR Manager", "HR System", PermissionLevel.ADMIN, true));
        rolePolicyRepository.save(new RolePolicy("HR Manager", "Jira", PermissionLevel.USER, true));

        // Finance Analyst
        rolePolicyRepository.save(new RolePolicy("Finance Analyst", "Finance System", PermissionLevel.READ, true));
        rolePolicyRepository.save(new RolePolicy("Finance Analyst", "Jira", PermissionLevel.USER, true));

        // Product Manager
        rolePolicyRepository.save(new RolePolicy("Product Manager", "Jira", PermissionLevel.ADMIN, true));
        rolePolicyRepository.save(new RolePolicy("Product Manager", "GitHub", PermissionLevel.READ, true));

        // System Administrator
        rolePolicyRepository.save(new RolePolicy("System Administrator", "AWS Console", PermissionLevel.ADMIN, true));
        rolePolicyRepository.save(new RolePolicy("System Administrator", "GitHub", PermissionLevel.ADMIN, true));
    }

    private void seedEmployeesAndEntitlements() {
        LocalDateTime now = LocalDateTime.now();

        // Specific Synthetic Test Case 1: Mover (Developer -> HR Manager) with leftover GitHub ADMIN
        createEmployeeWithAccess(
                "EMP-1001", "usr_alex", "Alex Mercer", "alex.mercer@saas.com", "HR",
                "HR Manager", "Developer", EmploymentStatus.ACTIVE,
                now.minusYears(2), now.minusDays(10),
                Map.of("HR System", PermissionLevel.ADMIN, "GitHub", PermissionLevel.ADMIN, "Jira", PermissionLevel.USER),
                Map.of("GitHub", PermissionLevel.ADMIN),
                List.of(new ApprovalRecord("APP-101", "usr_alex", "HR System", PermissionLevel.ADMIN, "usr_alex", "sec_admin", now.minusDays(10), ApprovalStatus.APPROVED, "Role transition approval"))
        );

        // Specific Synthetic Test Case 2: Leaver (LEFT) with active VPN & Finance access
        createEmployeeWithAccess(
                "EMP-1002", "usr_sarah", "Sarah Jenkins", "sarah.j@saas.com", "Finance",
                "Finance Analyst", null, EmploymentStatus.LEFT,
                now.minusYears(3), null,
                Map.of("Finance System", PermissionLevel.WRITE, "AWS Console", PermissionLevel.READ),
                Map.of("Finance System", PermissionLevel.WRITE),
                List.of()
        );

        // Specific Synthetic Test Case 3: Active User with Unapproved AWS ADMIN access
        createEmployeeWithAccess(
                "EMP-1003", "usr_david", "David Vance", "david.v@saas.com", "Engineering",
                "Developer", null, EmploymentStatus.ACTIVE,
                now.minusYears(1), null,
                Map.of("GitHub", PermissionLevel.WRITE, "AWS Console", PermissionLevel.ADMIN),
                Map.of("GitHub", PermissionLevel.WRITE),
                List.of(new ApprovalRecord("APP-103", "usr_david", "GitHub", PermissionLevel.WRITE, "usr_david", "lead_eng", now.minusMonths(6), ApprovalStatus.APPROVED, "Standard onboarding"))
        );

        // Specific Synthetic Test Case 4: Leaver with Critical ADMIN Access
        createEmployeeWithAccess(
                "EMP-1004", "usr_marcus", "Marcus Brody", "marcus.b@saas.com", "IT Security",
                "System Administrator", null, EmploymentStatus.LEFT,
                now.minusYears(4), null,
                Map.of("AWS Console", PermissionLevel.ADMIN, "GitHub", PermissionLevel.ADMIN),
                Map.of("AWS Console", PermissionLevel.ADMIN),
                List.of()
        );

        // Seed remaining synthetic employees (EMP-1005 to EMP-1040)
        String[] departments = {"Engineering", "HR", "Finance", "Product", "Operations"};
        String[] roles = {"Developer", "HR Manager", "Finance Analyst", "Product Manager", "System Administrator"};

        for (int i = 5; i <= 40; i++) {
            String empId = "EMP-" + (1000 + i);
            String userId = "usr_user" + i;
            String name = "User " + i + " " + (i % 2 == 0 ? "Smith" : "Johnson");
            String email = "user" + i + "@saas.com";
            String dept = departments[i % departments.length];
            String role = roles[i % roles.length];
            EmploymentStatus status = (i % 8 == 0) ? EmploymentStatus.LEFT : ((i % 12 == 0) ? EmploymentStatus.ON_LEAVE : EmploymentStatus.ACTIVE);

            Map<String, PermissionLevel> appMap = new HashMap<>();
            if (role.equals("Developer")) {
                appMap.put("GitHub", (i % 5 == 0) ? PermissionLevel.ADMIN : PermissionLevel.WRITE);
                appMap.put("Jira", PermissionLevel.USER);
            } else if (role.equals("HR Manager")) {
                appMap.put("HR System", PermissionLevel.ADMIN);
                appMap.put("Jira", PermissionLevel.USER);
            } else if (role.equals("Finance Analyst")) {
                appMap.put("Finance System", PermissionLevel.READ);
                appMap.put("Jira", PermissionLevel.USER);
            } else if (role.equals("Product Manager")) {
                appMap.put("Jira", PermissionLevel.ADMIN);
                appMap.put("GitHub", PermissionLevel.READ);
            } else {
                appMap.put("AWS Console", PermissionLevel.ADMIN);
                appMap.put("GitHub", PermissionLevel.ADMIN);
            }

            // Seed valid approval for compliant active users
            List<ApprovalRecord> appList = new ArrayList<>();
            if (status == EmploymentStatus.ACTIVE && i % 5 != 0) {
                for (String appName : appMap.keySet()) {
                    appList.add(new ApprovalRecord("APP-" + i + "-" + appName.hashCode(), userId, appName, appMap.get(appName), userId, "manager_admin", now.minusMonths(3), ApprovalStatus.APPROVED, "Standard role entitlement"));
                }
            }

            createEmployeeWithAccess(empId, userId, name, email, dept, role, null, status, now.minusMonths(i), null, appMap, Map.of(), appList);
        }
    }

    private void createEmployeeWithAccess(String empId, String userId, String name, String email, 
                                           String dept, String currentRole, String prevRole, 
                                           EmploymentStatus status, LocalDateTime joinDate, LocalDateTime roleChangeDate,
                                           Map<String, PermissionLevel> appEntitlements,
                                           Map<String, PermissionLevel> dirGroups,
                                           List<ApprovalRecord> approvals) {
        LocalDateTime now = LocalDateTime.now();
        Employee emp = new Employee(empId, userId, name, email, dept, currentRole, prevRole, status, joinDate, roleChangeDate, now);
        employeeRepository.save(emp);

        for (Map.Entry<String, PermissionLevel> entry : appEntitlements.entrySet()) {
            Entitlement ent = new Entitlement(userId, entry.getKey(), entry.getValue(), joinDate, "ACTIVE", now);
            entitlementRepository.save(ent);
        }

        for (Map.Entry<String, PermissionLevel> entry : dirGroups.entrySet()) {
            DirectoryGroup group = new DirectoryGroup(userId, entry.getKey() + "_Group", entry.getKey(), entry.getValue(), now);
            directoryGroupRepository.save(group);
        }

        if (approvals != null) {
            approvalRecordRepository.saveAll(approvals);
        }
    }
}
