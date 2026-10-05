# Reproducible Build & Execution Runtime Evidence

**Project:** JML Access Reconciliation Engine  
**Execution Timestamp:** 2026-10-05  
**Build Status:** PASSED (100% Test Pass Rate, 0 Failures, 0 Errors)

---

## 1. Unit & Integration Test Suite Verification

The Spring Boot test suite was executed locally via Maven. All 10 unit and integration tests passed cleanly.

### Execution Command:
```bash
cd backend
mvn test
```

### Verified Test Suite Breakdown:
1. `testOrphanedAccessLeaverDetection` — **PASSED** (Verifies detection of retained entitlements for terminated employees).
2. `testExcessiveAccessMoverDetection` — **PASSED** (Verifies detection of leftover admin privileges for department movers).
3. `testUnapprovedPrivilegedAccessDetection` — **PASSED** (Verifies detection of active users with unapproved admin entitlements).
4. `testSelfApprovalPrevention` — **PASSED** (Verifies accountable dual-control gate blocks self-approval attempts).
5. `testRemediationExecutionAndRollback` — **PASSED** (Verifies automated state modification and instant rollback restoration).
6. `testDataFeedDegradationConfidenceScore` — **PASSED** (Verifies dynamic confidence score drops appropriately on feed loss).
7. `testMultiTrialExperimentEvaluation` — **PASSED** (Verifies empirical multi-run variance & 95% confidence interval calculations).
8. `testStakeholderValidationSignOff` — **PASSED** (Verifies stakeholder compliance sign-off stamping on remediation evidence).
9. `testEvidenceTrailCsvExport` — **PASSED** (Verifies automated CSV generation for audit evidence trails).
10. `testRealTimeNotificationAlerts` — **PASSED** (Verifies real-time SLA breach & anomaly alert broadcasting).

**Test Output Artifact:** Recorded in `backend/build_evidence/mvn_test_output.txt`.

---

## 2. Standalone Application Package Verification

### Execution Command:
```bash
cd backend
mvn clean package -DskipTests
```

### Build Result:
- Target Artifact: `backend/target/reconciliation-engine-1.0.0.jar`
- Size: ~38 MB (repackaged Spring Boot executable jar with embedded Tomcat and H2 database).

---

## 3. Containerized Stack Bootstrapping (`docker-compose`)

### Configuration File: `docker-compose.yml`

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=default
    restart: always

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "5173:80"
    depends_on:
      - backend
    restart: always
```

### How to Run:
```bash
docker-compose up --build
```

### Endpoints Verified:
- **Frontend Dashboard UI:** `http://localhost:5173`
- **Backend API Base:** `http://localhost:8080/api`
- **H2 In-Memory Database Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:jmldb`)
- **Real-Time Notification Stream:** `http://localhost:8080/api/notifications/stream`
