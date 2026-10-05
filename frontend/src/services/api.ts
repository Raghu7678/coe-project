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

const API_BASE = 'http://localhost:8080/api';

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

export const api = {
  // Dashboard & Contractors
  getDashboardSummary: (): Promise<DashboardSummary> =>
    fetch(`${API_BASE}/dashboard/summary`).then((r) => handleResponse<DashboardSummary>(r)),

  getContractors: (): Promise<Contractor[]> =>
    fetch(`${API_BASE}/contractors`).then((r) => handleResponse<Contractor[]>(r)),

  getContractorByCode: (code: string): Promise<Contractor> =>
    fetch(`${API_BASE}/contractors/${code}`).then((r) => handleResponse<Contractor>(r)),

  // Risk Assessment & Anomalies
  runRiskAssessment: (engine: 'PROTOTYPE' | 'BASELINE' = 'PROTOTYPE'): Promise<ContractorRiskAnomaly[]> =>
    fetch(`${API_BASE}/reconciliation/run?engine=${engine}`, { method: 'POST' }).then((r) => handleResponse<ContractorRiskAnomaly[]>(r)),

  getAnomalies: (engine: 'PROTOTYPE' | 'BASELINE' = 'PROTOTYPE'): Promise<ContractorRiskAnomaly[]> =>
    fetch(`${API_BASE}/reconciliation/issues?engine=${engine}`).then((r) => handleResponse<ContractorRiskAnomaly[]>(r)),

  getAnomalyById: (id: number): Promise<ContractorRiskAnomaly> =>
    fetch(`${API_BASE}/reconciliation/issues/${id}`).then((r) => handleResponse<ContractorRiskAnomaly>(r)),

  // Approvals
  getPendingApprovals: (): Promise<QualificationAction[]> =>
    fetch(`${API_BASE}/approvals/pending`).then((r) => handleResponse<QualificationAction[]>(r)),

  processApprovalDecision: (id: number, reviewerId: string, decision: 'APPROVED' | 'REJECTED', comments: string): Promise<QualificationAction> =>
    fetch(`${API_BASE}/approvals/${id}/decision`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reviewerId, decision, comments })
    }).then((r) => handleResponse<QualificationAction>(r)),

  // Qualification Actions & Rollback & Validation
  getAllActions: (): Promise<QualificationAction[]> =>
    fetch(`${API_BASE}/remediations`).then((r) => handleResponse<QualificationAction[]>(r)),

  executeAction: (id: number): Promise<QualificationAction> =>
    fetch(`${API_BASE}/remediations/${id}/execute`, { method: 'POST' }).then((r) => handleResponse<QualificationAction>(r)),

  rollbackAction: (id: number, actor: string = 'ADMIN', reason: string = 'Manual override'): Promise<QualificationAction> =>
    fetch(`${API_BASE}/remediations/${id}/rollback`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ actor, reason })
    }).then((r) => handleResponse<QualificationAction>(r)),

  validateAction: (id: number, validatedBy: string, validationNotes: string): Promise<QualificationAction> =>
    fetch(`${API_BASE}/remediations/${id}/validate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ validatedBy, validationNotes })
    }).then((r) => handleResponse<QualificationAction>(r)),

  // Export URLs
  getRemediationCsvExportUrl: () => `${API_BASE}/remediations/export/csv`,
  getRemediationPdfExportUrl: () => `${API_BASE}/remediations/export/pdf`,
  getAuditCsvExportUrl: () => `${API_BASE}/audit/export/csv`,

  // Audit Logs
  getAuditLogs: (): Promise<AuditEvent[]> =>
    fetch(`${API_BASE}/audit`).then((r) => handleResponse<AuditEvent[]>(r)),

  // Data Source Health
  getDataSourceHealth: (): Promise<DataSourceHealth[]> =>
    fetch(`${API_BASE}/data-sources/health`).then((r) => handleResponse<DataSourceHealth[]>(r)),

  updateDataSourceHealth: (sourceName: string, status: HealthState, latencyMs?: number, stalenessHours?: number): Promise<DataSourceHealth> =>
    fetch(`${API_BASE}/data-sources/health/${sourceName}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status, latencyMs, stalenessHours })
    }).then((r) => handleResponse<DataSourceHealth>(r)),

  // Evaluation & Metrics
  runEvaluationExperiment: (): Promise<EvaluationMetrics[]> =>
    fetch(`${API_BASE}/evaluation/run`).then((r) => handleResponse<EvaluationMetrics[]>(r)),

  getMultiTrialExperiment: (trials: number = 10): Promise<ExperimentResult> =>
    fetch(`${API_BASE}/evaluation/multi-run?trials=${trials}`).then((r) => handleResponse<ExperimentResult>(r)),

  // Notifications
  getNotifications: (): Promise<NotificationAlert[]> =>
    fetch(`${API_BASE}/notifications`).then((r) => handleResponse<NotificationAlert[]>(r)),

  // Policy Management
  getPolicy: (): Promise<RiskThresholdPolicy> =>
    fetch(`${API_BASE}/policy`).then((r) => handleResponse<RiskThresholdPolicy>(r)),

  updatePolicy: (policy: RiskThresholdPolicy): Promise<RiskThresholdPolicy> =>
    fetch(`${API_BASE}/policy`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(policy)
    }).then((r) => handleResponse<RiskThresholdPolicy>(r))
};
