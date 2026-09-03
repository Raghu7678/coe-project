# Three-Minute Demo Script

## Project Title
**JML Access Reconciliation Engine with Accountable Approvals, Audit Trail, and Rollback**

---

## Demo Overview & Presentation Guide (3:00 Minutes)

### 0:00–0:30 | Problem Statement & Context
* **Presenter Action**: Open the Dashboard (`http://localhost:5173`).
* **Script**: 
  > "Welcome. In modern SaaS companies, Joiner-Mover-Leaver (JML) access management is a major security challenge. When employees leave or transition between roles, legacy removal requests are frequently delayed. This creates dangerous orphaned access, excessive privileges, and unapproved access across SaaS platforms like GitHub, AWS, and HR systems.
  > Today, we present the **JML Access Reconciliation Engine**—a multi-source, resilient reconciliation solution that automatically detects access anomalies, calculates confidence scores based on data source health, enforces accountable human approval for high-risk removals, and supports full audit rollback."

---

### 0:30–1:00 | Data Sources & Multi-Source Health Monitoring
* **Presenter Action**: Navigate to **Data Source Health** tab.
* **Script**:
  > "Notice our active data sources: HR System, Directory Groups, Application Entitlements, and Approval History. 
  > Unlike legacy tools that crash or issue false alarms when a data feed fails, our engine actively evaluates data freshness and health. When a source is delayed or stale, the engine dynamically adjusts its confidence score and engages auto-remediation safety gates to prevent unsafe automatic removals."

---

### 1:00–1:40 | Demonstrating a Mover Role Transition & Reconciliation
* **Presenter Action**: Navigate to **Reconciliation** tab. Show `Alex Mercer` (usr_alex).
* **Script**:
  > "Let's observe a real Mover event: Employee Alex Mercer recently transitioned from **Developer** to **HR Manager**. 
  > In a traditional setup, his old GitHub ADMIN access might linger indefinitely. Let's run our prototype reconciliation engine...
  > Instantly, the engine flags Alex's leftover GitHub ADMIN access as an **EXCESSIVE_ACCESS** / **ROLE_CHANGE_ACCESS_CONFLICT** issue with a **HIGH** risk level. Notice that because GitHub ADMIN is a privileged permission, the engine sets the remediation status to `PENDING_REVIEW` rather than auto-deleting it."

---

### 1:40–2:10 | Accountable Approval Workflow & Execution
* **Presenter Action**: Navigate to **Approval Queue**. Show pending item for `Alex Mercer`. Select reviewer `sec_reviewer`, enter comment, click **Approve & Execute**.
* **Script**:
  > "Now, we step into the **Accountable Approval Queue**. Here, security reviewers inspect the evidence: previous state `Permission: ADMIN` versus expected state `Permission: WRITE`. 
  > To guarantee accountability, our system enforces self-approval prevention. As security reviewer `sec_reviewer`, I approve the action with rationale. 
  > Upon approval, the engine immediately updates Alex's entitlement state in the target system, reducing his permission to WRITE and recording an immutable audit event."

---

### 2:10–2:35 | Simulating Data Source Failure & Engine Resilience
* **Presenter Action**: Navigate to **Data Source Health**. Set **DIRECTORY** source to `UNAVAILABLE`. Re-run reconciliation.
* **Script**:
  > "Now let's simulate a real-world outage: What happens if the Directory Group service goes completely offline?
  > We toggle Directory to `UNAVAILABLE` and re-run reconciliation. 
  > The engine does NOT crash. Instead, it continues operating using available HR, Entitlement, and Approval sources, while dropping the confidence score for affected users to 70%. Because confidence is below our 75% safety threshold, destructive removals are automatically redirected to human review."

---

### 2:35–3:00 | Audit Trail, Rollback & Empirical Baseline Comparison
* **Presenter Action**: 
  1. Navigate to **Remediation & Rollback** page. Click **Rollback** on Alex Mercer's executed action. Provide reason: *"Temporary emergency project access authorized by VP"*.
  2. Navigate to **Audit Trail** page to show append-only `ROLLBACK_EXECUTED` event.
  3. Navigate to **Baseline vs Prototype** evaluation page.
* **Script**:
  > "Finally, if a removal was made by mistake, our authorized administrator can trigger an instant **Rollback**. The previous permission state is restored, and a new `ROLLBACK_EXECUTED` event is appended to our immutable audit log—never deleting history.
  > Comparing our improved prototype against a naive baseline on the exact same synthetic dataset, the baseline achieved only 50% recall and missed unapproved access. Our resilient prototype achieves **100% precision and recall** with SLA compliance rates exceeding 95%.
  > Thank you!"
