# Demonstration & Scoring Script

This script provides a step-by-step walkthrough for evaluating the **Contractor Risk Pre-Qualification Tool**.

---

## 1. Startup & System Verification
1. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   *Verify API status at `http://localhost:8080/` (Returns `UP` status).*

2. **Start Frontend**:
   ```bash
   cd frontend
   npm run dev
   ```
   *Open browser at `http://localhost:5173`.*

---

## 2. Step-by-Step Evaluation Walkthrough

### Step 1: Executive Dashboard Overview
* Navigate to **Executive Dashboard**.
* Observe key metrics:
  * Total Contractors: **40**
  * High/Critical Risk Flags
  * Data Feed Confidence Score: **100%**
  * Feeds Health Matrix: `FINANCIAL_CREDIT_FEED`, `SAFETY_OSHA_FEED`, `INSURANCE_COMPLIANCE_FEED`, `SANCTIONS_WATCHLIST_FEED` (`AVAILABLE`).

---

### Step 2: Contractor Risk Evaluation Hub
* Navigate to **Risk Evaluation Hub**.
* Click **Run Assessment** (Prototype Engine).
* Observe **12 detected risk anomalies**:
  1. `BuildTech Heavy Dynamics` — **HIGH_EMR_SAFETY_VIOLATION** (EMR=1.45, 1 Fatality).
  2. `Sentinel Security Corp` — **SANCTION_WATCHLIST_MATCH** (Matched PEP/OFAC List).
  3. `Titan Civil Construction` — **EXPIRED_INSURANCE_DEFICIT** (Expired COI).
  4. `Omni Freight Freightlines` — **FINANCIAL_INSOLVENCY_RISK** (Credit score 38).
* Click **Inspect** on `BuildTech Heavy Dynamics` to view the multi-source evidence details.

---

### Step 3: Accountable Approval Queue (Dual-Control Enforcement)
* Navigate to **Approval Queue**.
* Select a pending review item (e.g. `BuildTech Heavy Dynamics`).
* Attempt self-approval:
  * Set Reviewer ID to `RISK_ENGINE` (same as initiator).
  * Click **APPROVE QUALIFICATION ACTION**.
  * Observe error banner: *"Self-approval is strictly forbidden under dual-control accountable approval policy."*
* Change Reviewer ID to `RISK_OFFICER_01` and submit approval.
* Observe success notification.

---

### Step 4: Qualification Remediation & Instant Rollback
* Navigate to **Remediation & Rollback**.
* Click **Execute** on the approved action. Notice status transition to `EXECUTED`.
* Click **Rollback** on the executed action, enter auditor rationale, and click **Confirm Rollback**.
* Notice vendor status is instantly restored to previous qualification state.

---

### Step 5: Data Feeds Health Simulation
* Navigate to **Data Feeds Health**.
* Under `SAFETY_OSHA_FEED`, click **UNAVAILABLE**.
* Notice dynamic Data Confidence Score drops from **100%** to **60%**, triggering safety gates across all vendor assessments.

---

### Step 6: Empirical Evaluation Metrics (Baseline vs Prototype)
* Navigate to **Baseline vs Prototype**.
* Click **Re-Run Experiment**.
* Review comparative metrics table:
  * **Naive Baseline Engine**: 75% Precision, 50% Recall (missed safety & sanctions).
  * **Improved Prototype Engine**: **100% Precision**, **100% Recall**, **95% Target SLA Compliance**.
