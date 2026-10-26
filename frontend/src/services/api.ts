import {
  Contractor,
  ContractorRiskAnomaly,
  QualificationAction,
  DataSourceHealth,
  AuditEvent,
  DashboardSummary,
  EvaluationMetrics,
  ExperimentResult,
  NotificationAlert,
  RiskThresholdPolicy,
  HealthState
} from '../types';

/**
 * Base URL pointing to the Spring Boot REST API.
 */
const API_BASE = 'http://localhost:8080/api';

/**
 * Generic response handler parsing JSON payloads and translating HTTP errors
 * into descriptive Error instances with backend-provided RFC 7807 error messages.
 */
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text();
    let message = `API Error ${response.status}`;
    try {
      const json = JSON.parse(errorText);
      if (json.message) message = json.message;
    } catch {
      if (errorText) message = errorText;
    }
    throw new Error(message);
  }
  const data = await response.json();
  return data as T;
}

/**
 * Client API interface connecting the React UI to the Spring Boot reconciliation backend.
 */
export const api = {
  // ==========================================
  // Dashboard & Personnel / Contractors
  // ==========================================

  /**
   * Retrieves aggregate dashboard metrics (total identities, active, leavers, high-risk counts, confidence).
   */
  getDashboardSummary: (): Promise<DashboardSummary> =>
    fetch(`${API_BASE}/dashboard/summary`).then((r) => handleResponse<DashboardSummary>(r)),

  /**
   * Lists all 40 pre-seeded identities (employees / contractors).
   */
  getContractors: (): Promise<Contractor[]> =>
    fetch(`${API_BASE}/contractors`).then((r) => handleResponse<Contractor[]>(r)),

  /**
   * Retrieves a single identity by username or employee code.
   */
  getContractorByCode: (code: string): Promise<Contractor> =>
    fetch(`${API_BASE}/contractors/${code}`).then((r) => handleResponse<Contractor>(r)),

  // ==========================================
  // Risk Assessment & Anomaly Detection
  // ==========================================

  /**
   * Triggers an on-demand reconciliation / risk assessment run using either PROTOTYPE or BASELINE engine.
   */
  runRiskAssessment: (engine: 'PROTOTYPE' | 'BASELINE' = 'PROTOTYPE'): Promise<ContractorRiskAnomaly[]> =>
    fetch(`${API_BASE}/reconciliation/run?engine=${engine}`, { method: 'POST' }).then((r) => handleResponse<ContractorRiskAnomaly[]>(r)),

  /**
   * Fetches all detected access anomalies for the selected engine.
   */
  getAnomalies: (engine: 'PROTOTYPE' | 'BASELINE' = 'PROTOTYPE'): Promise<ContractorRiskAnomaly[]> =>
    fetch(`${API_BASE}/reconciliation/issues?engine=${engine}`).then((r) => handleResponse<ContractorRiskAnomaly[]>(r)),

  /**
   * Inspects detailed multi-source evidence and confidence scores for a specific anomaly ID.
   */
  getAnomalyById: (id: number): Promise<ContractorRiskAnomaly> =>
    fetch(`${API_BASE}/reconciliation/issues/${id}`).then((r) => handleResponse<ContractorRiskAnomaly>(r)),

  // ==========================================
  // Accountable Approvals (Dual-Control)
  // ==========================================

  /**
   * Retrieves all remediation actions currently held in the safety gate approval queue.
   */
  getPendingApprovals: (): Promise<QualificationAction[]> =>
    fetch(`${API_BASE}/approvals/pending`).then((r) => handleResponse<QualificationAction[]>(r)),

  /**
   * Submits an approval or rejection decision. Enforces non-self-approval dual-control policy.
   */
  processApprovalDecision: (id: number, reviewerId: string, decision: 'APPROVED' | 'REJECTED', comments: string): Promise<QualificationAction> =>
    fetch(`${API_BASE}/approvals/${id}/decision`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reviewerId, decision, comments })
    }).then((r) => handleResponse<QualificationAction>(r)),

  // ==========================================
  // Qualification Remediation & State Rollback
  // ==========================================

  /**
   * Lists all remediation actions and their current lifecycle statuses.
   */
  getAllActions: (): Promise<QualificationAction[]> =>
    fetch(`${API_BASE}/remediations`).then((r) => handleResponse<QualificationAction[]>(r)),

  /**
   * Executes an approved remediation action, updating actual application entitlements.
   */
  executeAction: (id: number): Promise<QualificationAction> =>
    fetch(`${API_BASE}/remediations/${id}/execute`, { method: 'POST' }).then((r) => handleResponse<QualificationAction>(r)),

  /**
   * Performs instant status rollback restoring previous permission levels with zero data loss.
   */
  rollbackAction: (id: number, actor: string = 'ADMIN', reason: string = 'Manual override'): Promise<QualificationAction> =>
    fetch(`${API_BASE}/remediations/${id}/rollback`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ actor, reason })
    }).then((r) => handleResponse<QualificationAction>(r)),

  /**
   * Attaches digital stakeholder validation signoff to an executed removal evidence trail.
   */
  validateAction: (id: number, validatedBy: string, validationNotes: string): Promise<QualificationAction> =>
    fetch(`${API_BASE}/remediations/${id}/validate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ validatedBy, validationNotes })
    }).then((r) => handleResponse<QualificationAction>(r)),

  // ==========================================
  // Export URLs
  // ==========================================
  getRemediationCsvExportUrl: () => `${API_BASE}/remediations/export/csv`,
  getRemediationPdfExportUrl: () => `${API_BASE}/remediations/export/pdf`,
  getAuditCsvExportUrl: () => `${API_BASE}/audit/export/csv`,

  // ==========================================
  // Audit Logs
  // ==========================================

  /**
   * Retrieves the append-only immutable audit trail log.
   */
  getAuditLogs: (): Promise<AuditEvent[]> =>
    fetch(`${API_BASE}/audit`).then((r) => handleResponse<AuditEvent[]>(r)),

  // ==========================================
  // Data Source Health & Resilience Testing
  // ==========================================

  /**
   * Retrieves operational health, latency, and staleness of all upstream feeds.
   */
  getDataSourceHealth: (): Promise<DataSourceHealth[]> =>
    fetch(`${API_BASE}/data-sources/health`).then((r) => handleResponse<DataSourceHealth[]>(r)),

  /**
   * Overrides data feed health to simulate degraded scenarios (STALE, DELAYED, UNAVAILABLE).
   */
  updateDataSourceHealth: (sourceName: string, status: HealthState, latencyMs?: number, stalenessHours?: number): Promise<DataSourceHealth> =>
    fetch(`${API_BASE}/data-sources/health/${sourceName}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status, latencyMs, stalenessHours })
    }).then((r) => handleResponse<DataSourceHealth>(r)),

  // ==========================================
  // Evaluation & Benchmark Experiments
  // ==========================================

  /**
   * Runs single-shot comparative experiment (Baseline vs Prototype).
   */
  runEvaluationExperiment: (): Promise<EvaluationMetrics[]> =>
    fetch(`${API_BASE}/evaluation/run`).then((r) => handleResponse<EvaluationMetrics[]>(r)),

  /**
   * Runs multi-trial Monte Carlo simulation computing variance, 95% CIs, and MTTR vs SLA compliance.
   */
  getMultiTrialExperiment: (trials: number = 10): Promise<ExperimentResult> =>
    fetch(`${API_BASE}/evaluation/multi-run?trials=${trials}`).then((r) => handleResponse<ExperimentResult>(r)),

  // ==========================================
  // Notifications & Alerts
  // ==========================================

  /**
   * Fetches recent notification alerts.
   */
  getNotifications: (): Promise<NotificationAlert[]> =>
    fetch(`${API_BASE}/notifications`).then((r) => handleResponse<NotificationAlert[]>(r)),

  // ==========================================
  // Policy & Threshold Management
  // ==========================================

  /**
   * Retrieves active risk threshold limits and weighting configuration.
   */
  getPolicy: (): Promise<RiskThresholdPolicy> =>
    fetch(`${API_BASE}/policy`).then((r) => handleResponse<RiskThresholdPolicy>(r)),

  /**
   * Updates risk threshold limits and weighting configuration.
   */
  updatePolicy: (policy: RiskThresholdPolicy): Promise<RiskThresholdPolicy> =>
    fetch(`${API_BASE}/policy`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(policy)
    }).then((r) => handleResponse<RiskThresholdPolicy>(r))
};
