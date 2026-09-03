import {
  DashboardSummary,
  EvaluationMetrics,
  ReconciliationIssue,
  RemediationAction,
  AuditEvent,
  DataSourceHealth,
  RolePolicy,
  Employee,
  HealthState,
} from '../types';

const API_BASE = '/api';

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${url}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
    ...options,
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || `API error ${res.status}: ${res.statusText}`);
  }
  return res.json();
}

export const api = {
  // Dashboard & Summary
  getDashboardSummary: () => fetchJson<DashboardSummary>('/dashboard/summary'),

  // Reconciliation
  runReconciliation: (engine: 'PROTOTYPE' | 'BASELINE' = 'PROTOTYPE') =>
    fetchJson<{ engineType: string; issuesDetectedCount: number; executionTimeMs: number; issues: ReconciliationIssue[] }>(
      `/reconciliation/run?engine=${engine}`,
      { method: 'POST' }
    ),
  getIssues: (engine: 'PROTOTYPE' | 'BASELINE' = 'PROTOTYPE') =>
    fetchJson<ReconciliationIssue[]>(`/reconciliation/issues?engine=${engine}`),
  getIssueById: (id: string) => fetchJson<ReconciliationIssue>(`/reconciliation/issues/${id}`),

  // Approvals
  getPendingApprovals: () => fetchJson<RemediationAction[]>('/approvals/pending'),
  processApprovalDecision: (id: string, reviewerId: string, decision: 'APPROVE' | 'REJECT', comment: string) =>
    fetchJson<RemediationAction>(`/approvals/${id}/decision`, {
      method: 'POST',
      body: JSON.stringify({ reviewerId, decision, comment }),
    }),

  // Remediations & Rollback
  getAllRemediations: () => fetchJson<RemediationAction[]>('/remediations'),
  executeRemediation: (id: string, actor: string = 'sec_admin') =>
    fetchJson<RemediationAction>(`/remediations/${id}/execute?actor=${actor}`, { method: 'POST' }),
  rollbackRemediation: (id: string, actor: string, reason: string) =>
    fetchJson<RemediationAction>(`/remediations/${id}/rollback`, {
      method: 'POST',
      body: JSON.stringify({ actor, reason }),
    }),

  // Audit Trail
  getAuditTrail: (userId?: string, eventType?: string) => {
    const params = new URLSearchParams();
    if (userId) params.append('userId', userId);
    if (eventType) params.append('eventType', eventType);
    return fetchJson<AuditEvent[]>(`/audit?${params.toString()}`);
  },

  // Data Source Health
  getDataSourceHealth: () => fetchJson<DataSourceHealth[]>('/data-sources/health'),
  updateDataSourceHealth: (sourceName: string, status: HealthState, freshness?: string) =>
    fetchJson<DataSourceHealth>(`/data-sources/health/${sourceName}`, {
      method: 'PUT',
      body: JSON.stringify({ status, freshness }),
    }),

  // Policies
  getPolicies: () => fetchJson<RolePolicy[]>('/policies'),
  savePolicy: (policy: RolePolicy) =>
    fetchJson<RolePolicy>('/policies', {
      method: 'POST',
      body: JSON.stringify(policy),
    }),
  deletePolicy: (id: number) =>
    fetch(`/api/policies/${id}`, { method: 'DELETE' }),

  // Evaluation
  runEvaluation: () => fetchJson<EvaluationMetrics>('/evaluation/run'),

  // Employees
  getEmployees: () => fetchJson<Employee[]>('/employees'),
};
