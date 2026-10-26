# Granular Technical Documentation: Unit Testing and Error Boundaries

**Project:** JML Access Reconciliation & Risk Pre-Qualification Engine with Accountable Approvals, Audit Trail, and Rollback  
**Version:** 1.0.0  
**Target Review:** Final Technical Architecture & Code Quality Review  

---

## Executive Summary

Enterprise identity access reconciliation and contractor risk pre-qualification systems manage high-impact privileges (e.g., AWS Console Administrator, production GitHub access, financial systems). In such environments, unhandled software exceptions, brittle error handling, or untested edge conditions can cause either catastrophic premature access revocation (breaking business operations) or security blind spots (allowing terminated leavers to retain active administrator credentials).

This document provides granular technical documentation on:
1. **The Automated Unit Testing Strategy**: A defensible test harness testing domain rules, multi-source feed degradation, dual-control enforcement, rollback idempotence, and statistical evaluation.
2. **The Dual-Layer Error Boundary Architecture**: Both frontend (React Error Boundaries with self-healing recovery and diagnostic telemetry) and backend (Spring Boot `@RestControllerAdvice` RFC 7807 compliant API error boundaries with sensitive data sanitization).

---

## 1. Unit Testing Strategy & Architecture

### 1.1 Testing Stack & Configuration

The automated test suite is implemented using modern Java enterprise testing standards:
* **Test Framework**: JUnit 5 (Jupiter API) with declarative `@DisplayName` and `@Test` annotations.
* **Spring Integration**: `@SpringBootTest` configured with the `@ActiveProfiles("default")` profile.
* **Database Isolation**: `@Transactional` test execution against the H2 in-memory database (`jdbc:h2:mem:jmldb`), guaranteeing that mutations during tests are automatically rolled back, preventing test pollution across test executions.
* **Data Fixture**: Controlled synthetic baseline seeded by [`DataInitializer.java`](file:///Users/raghu/Downloads/coe%20project/backend/src/main/java/com/jml/reconciliation/config/DataInitializer.java), establishing ground-truth anomalies with known outcomes.

---

### 1.2 Granular Breakdown of Test Cases

The primary test suite [`JmlReconciliationEngineTests.java`](file:///Users/raghu/Downloads/coe%20project/backend/src/test/java/com/jml/reconciliation/JmlReconciliationEngineTests.java) contains 13 focused unit and integration tests covering functional correctness, security policies, mathematical bounds, and resilience:

| Test Case Name | Method Name | Target Component | Scenario & Validation Logic |
| :--- | :--- | :--- | :--- |
| **Orphaned Leaver Detection** | `testOrphanedAccessLeaverDetection()` | `ReconciliationEngineService`, `ReconciliationIssueRepository` | Verifies that terminated employee Sarah Jenkins (`usr_sarah`, status: `LEFT`) holding active `Finance System` write permissions is detected as `ORPHANED_ACCESS_LEAVER`. |
| **Excessive Role Mover Detection** | `testExcessiveAccessMoverDetection()` | `ReconciliationEngineService`, `RolePolicyRepository` | Verifies that department mover Alex Mercer (`usr_alex`, moved from Developer to HR Manager) retaining leftover `GitHub` `ADMIN` permissions without documented transfer approval is flagged as `EXCESSIVE_ACCESS_MOVER`. |
| **Unapproved Privileged Access Detection** | `testUnapprovedPrivilegedAccessDetection()` | `ReconciliationEngineService`, `ApprovalRecordRepository` | Verifies that active developer David Vance (`usr_david`) holding `AWS Console` `ADMIN` access without an associated approval record in `approval_records` is flagged as `UNAPPROVED_PRIVILEGED_ACCESS`. |
| **Dual-Control Self-Approval Prevention** | `testSelfApprovalPrevention()` | `ApprovalService` | Simulates an initiator attempting to self-approve their own queued remediation action (`reviewerId == initiatedBy`). Verifies that the service throws `IllegalArgumentException` with message *"Self-approval is strictly forbidden under dual-control accountable approval policy."* |
| **Remediation Execution & Rollback** | `testRemediationExecutionAndRollback()` | `RemediationService`, `EntitlementRepository` | Tests full lifecycle: (1) Approved by distinct Security Officer, (2) Executed to revoke entitlement, (3) Instant rollback triggered restoring previous entitlement permission in `EntitlementRepository`. |
| **Feed Degradation Confidence Score** | `testDataFeedDegradationConfidenceScore()` | `DataSourceHealthService`, `ReconciliationEngineService` | Tests dynamic confidence calculation: marks the HR feed as `UNAVAILABLE`. Verifies confidence score drops from $1.00$ to $0.60$ ($1.0 - 0.40$). |
| **Confidence Score Lower-Bound Floor** | `testConfidenceScoreLowerBoundFloor()` | `ReconciliationEngineService`, `DataSourceHealthRepository` | Marks all upstream feeds as `UNAVAILABLE` (potential negative score: $1.0 - 4 \times 0.40 = -0.60$). Asserts that the calculation clamps to $0.00$ and never produces a negative value. |
| **Critical Leaver 15m SLA Target** | `testCriticalLeaverSlaTargetMinutes()` | `ReconciliationEngineService` | Evaluates critical leaver Marcus Brody (`usr_marcus`, `LEFT` with `AWS Console` `ADMIN`). Asserts risk level is `CRITICAL`, SLA target is exactly `15` minutes, and safety gate is triggered. |
| **Multi-Trial Monte Carlo Experiment** | `testMultiTrialExperimentEvaluation()` | `EvaluationService` | Executes $N=10$ trial simulation. Asserts sample mean precision $> 95\%$, mean recall $> 90\%$, non-empty SLA performance breakdowns, and confidence-band error analyses. |
| **Stakeholder Validation Sign-off** | `testStakeholderValidationSignOff()` | `RemediationService`, `AuditService` | Validates digital stamping on an executed remediation. Asserts `stakeholderValidated = true`, `validatedBy = "AUDITOR_BOB"`, and valid timestamp. |
| **Evidence Trail CSV Export** | `testEvidenceTrailCsvExport()` | `ExportService` | Generates removal evidence CSV. Asserts RFC 4180 format, non-null byte content, and presence of standard audit headers. |
| **Real-Time Notification Alerts** | `testRealTimeNotificationAlerts()` | `NotificationService` | Queries memory notification buffer. Asserts seeded alerts contain `CRITICAL` severity warnings and proper target tab metadata. |
| **API Error Boundary Exception Translation** | `testGlobalExceptionHandlerApiErrorBoundary()` | `GlobalExceptionHandler` | Directly passes domain exceptions to `GlobalExceptionHandler`: verifies 400 Bad Request with code `DUAL_CONTROL_SELF_APPROVAL_VIOLATION`, 409 Conflict with code `INVALID_LIFECYCLE_STATE`, and 404 Not Found with code `RESOURCE_NOT_FOUND`. |
| **Risk Threshold Policy Management** | `testRiskThresholdPolicyManagement()` | `PolicyService` | Retrieves active policy, modifies threshold values (EMR, credit score, confidence gate), validates bounds checking (rejecting confidence $> 1.0$), and verifies updates. |

---

### 1.3 How to Run the Unit Test Suite

```bash
cd backend
# Execute all tests and generate Surefire test reports
mvn clean test

# Execute with detailed console output
mvn test -Dtest=JmlReconciliationEngineTests
```

---

## 2. Error Boundaries Architecture

Error boundaries are structured across both tiers of the application:
1. **Frontend (Presentation Tier)**: React Error Boundary components isolating rendering exceptions.
2. **Backend (Application Tier)**: Spring Boot `@RestControllerAdvice` converting exceptions into standardized problem details envelopes while shielding internal system internals.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        FRONTEND ERROR BOUNDARIES                       │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │               Top-Level App Shell ErrorBoundary                │   │
│   │  (Catches critical navbar/sidebar/window-level anomalies)      │   │
│   └───────────────────────────────┬────────────────────────────────┘   │
│                                   │                                    │
│   ┌───────────────────────────────▼────────────────────────────────┐   │
│   │              Main Workspace View ErrorBoundary                 │   │
│   │  (Isolates active tab: Dashboard, RiskAssessment, Approvals)   │   │
│   │  • If a chart/table throws: Nav remains interactive & clickable│   │
│   │  • Provides "Retry Component", "Copy Diagnostics", "Reload"    │   │
│   └────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP REST Requests
┌───────────────────────────────────▼────────────────────────────────────┐
│                         BACKEND ERROR BOUNDARIES                       │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │            GlobalExceptionHandler (@RestControllerAdvice)      │   │
│   ├────────────────────────────────────────────────────────────────┤   │
│   │  IllegalArgumentException   ──► 400 Bad Request (RFC 7807)     │   │
│   │  IllegalStateException      ──► 409 Conflict (State lifecycle) │   │
│   │  NoSuchElementException     ──► 404 Not Found (Missing ID)     │   │
│   │  MethodArgumentNotValidEx   ──► 422 Unprocessable Entity       │   │
│   │  Generic Exception          ──► 500 Sanitized Internal Error   │   │
│   └────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

---

### 2.1 Frontend React Error Boundary (`ErrorBoundary.tsx`)

#### Architectural Purpose
In standard React applications without error boundaries, an unhandled JavaScript exception thrown inside any child component's render method, lifecycle method, or constructor unmounts the entire React component tree, causing a blank white screen.

The [`ErrorBoundary.tsx`](file:///Users/raghu/Downloads/coe%20project/frontend/src/components/ErrorBoundary.tsx) component implements React's `static getDerivedStateFromError` and `componentDidCatch` lifecycle hooks:

```tsx
export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  public static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    const errorId = `ERR-${Date.now().toString(36).toUpperCase()}-${Math.random().toString(36).substring(2, 6).toUpperCase()}`;
    return { hasError: true, error, errorId };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    console.error('ErrorBoundary caught unhandled component anomaly:', error, errorInfo);
    this.setState({ errorInfo });
  }
  ...
}
```

#### Key Capabilities & Recovery Workflows:
1. **Hierarchical Fault Containment**:
   - In [`App.tsx`](file:///Users/raghu/Downloads/coe%20project/frontend/src/App.tsx), the `<main>` workspace is wrapped in a dedicated `ErrorBoundary`. If an error occurs while rendering a complex visual table or metric card in `EvaluationMetricsPage`, the navigation `<Sidebar>` and top `<Navbar>` remain interactive. The user can simply click another tab without losing application context.
2. **Self-Healing Reset ("Retry Component")**:
   - The user can click **Retry Component**, which clears `hasError`, resets component state, and invokes an optional `onReset` callback (e.g. re-fetching clean data from the API).
3. **Application Reload ("Reload Application")**:
   - Fallback button triggering `window.location.reload()` for clean cache re-initialization.
4. **Copyable Telemetry ("Copy Diagnostics")**:
   - Generates a structured JSON diagnostic packet containing the generated `errorId`, ISO timestamp, error name, message, stack trace, and React component hierarchy stack. Copies directly to the user's clipboard for incident ticket attachment.
5. **Cyber Dark Glassmorphic Aesthetic**:
   - Styled consistently with the application's cyber theme using Tailwind CSS and Lucide icons (`AlertOctagon`, `RefreshCw`, `RotateCcw`, `Copy`).

---

### 2.2 Backend Global Exception Handler & Error Envelopes

#### Architectural Purpose
When client requests violate domain rules (such as attempting self-approval or executing an unapproved action), raw unhandled exceptions can leak internal class names, database connection details, or framework stack traces.

The [`GlobalExceptionHandler.java`](file:///Users/raghu/Downloads/coe%20project/backend/src/main/java/com/jml/reconciliation/config/GlobalExceptionHandler.java) is annotated with `@RestControllerAdvice`, creating an application-wide HTTP error boundary.

#### Standardized Error Envelope (`ApiErrorResponse.java`):
Complies with RFC 7807 (Problem Details for HTTP APIs):
```json
{
  "timestamp": "2026-10-26T15:39:47.241",
  "status": 400,
  "error": "Bad Request",
  "message": "Self-approval is strictly forbidden under dual-control accountable approval policy.",
  "path": "/api/approvals/1/decision",
  "errorCode": "DUAL_CONTROL_SELF_APPROVAL_VIOLATION"
}
```

#### Exception Mapping Specification:

| Exception Class | HTTP Status | Error Code | Example Trigger Condition |
| :--- | :--- | :--- | :--- |
| `IllegalArgumentException` (Self-Approval) | **400 Bad Request** | `DUAL_CONTROL_SELF_APPROVAL_VIOLATION` | Reviewer ID submitted in `ApprovalDecisionRequest` matches `RemediationAction.initiatedBy`. |
| `IllegalArgumentException` (Generic) | **400 Bad Request** | `INVALID_REQUEST_ARGUMENT` | Request parameters invalid or out of bounds (e.g. confidence threshold $> 1.0$). |
| `IllegalStateException` | **409 Conflict** | `INVALID_LIFECYCLE_STATE` | Attempting to execute an action that is not in `APPROVED` status, or rolling back an action not in `EXECUTED` status. |
| `NoSuchElementException` | **404 Not Found** | `RESOURCE_NOT_FOUND` | Looking up a non-existent employee ID, issue ID, or remediation action ID. |
| `MethodArgumentNotValidException` | **422 Unprocessable Entity** | `VALIDATION_FAILED` | Request body payload fails Jakarta Bean Validation constraints (includes `validationErrors` map). |
| `Exception` (Generic catch-all) | **500 Internal Server Error** | `INTERNAL_SERVER_ERROR` | Unexpected runtime error. Detailed stack trace logged to server console; client receives sanitized error reference. |

---

## 3. Resilience & Failure Mode Matrix

| Scenario / Failure Mode | Injected State | Engine Response | Recovery / Outcome |
| :--- | :--- | :--- | :--- |
| **Upstream HR Feed Delay** | Status set to `DELAYED` (Latency: 2500ms, Staleness: 12h) | Confidence $C$ drops by $0.25$ ($C = 0.75$). | All subsequent remediation actions are flagged with safety gates and routed to `PENDING_APPROVAL`. |
| **Directory Feed Outage** | Status set to `UNAVAILABLE` | Confidence $C$ drops by $0.40$ ($C = 0.60$). | Automated instant execution is disabled; human sign-off required for all leavers. |
| **All Feeds Fail Simultaneously** | All 4 feeds set to `UNAVAILABLE` | Confidence calculation $1.0 - 4(0.40) = -0.60$ is clamped to $0.00$. | Engine halts auto-remediations; full safety gate enforcement. No negative confidence scores. |
| **Self-Approval Attempt** | Reviewer sets `reviewerId = initiatedBy` | Backend rejects request with HTTP 400 `DUAL_CONTROL_SELF_APPROVAL_VIOLATION`. | Frontend displays red warning banner; action remains safely in `PENDING_APPROVAL`. |
| **Rollback of Revoked Access** | Auditor triggers rollback on executed action | Restores original permission level from `previousPermissionLevel` into `EntitlementRepository`. | State restored with 100% fidelity; audit log captures actor and rationale. |

---

## 4. Verification Evidence

- **Automated Tests**: 13/13 JUnit tests passing with 0 errors and 0 failures.
- **Frontend Build**: `npm run build` completed cleanly (Vite production bundle built in $< 800\text{ms}$).
- **Backend Build**: `mvn clean test package` completed with `BUILD SUCCESS`.
