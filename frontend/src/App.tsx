import React, { useState, useEffect } from 'react';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';
import { DataSourceHealthBanner } from './components/DataSourceHealthBanner';

import { Dashboard } from './pages/Dashboard';
import { ReconciliationPage } from './pages/ReconciliationPage';
import { IssueDetailPage } from './pages/IssueDetailPage';
import { ApprovalQueuePage } from './pages/ApprovalQueuePage';
import { RemediationRollbackPage } from './pages/RemediationRollbackPage';
import { AuditTrailPage } from './pages/AuditTrailPage';
import { DataSourceHealthPage } from './pages/DataSourceHealthPage';
import { PolicyManagementPage } from './pages/PolicyManagementPage';
import { EvaluationMetricsPage } from './pages/EvaluationMetricsPage';

import {
  DashboardSummary,
  ReconciliationIssue,
  RemediationAction,
  AuditEvent,
  DataSourceHealth,
  RolePolicy,
  HealthState,
} from './types';
import { api } from './services/api';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [engineType, setEngineType] = useState<'PROTOTYPE' | 'BASELINE'>('PROTOTYPE');
  const [isReconciling, setIsReconciling] = useState(false);

  // Data states
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [issues, setIssues] = useState<ReconciliationIssue[]>([]);
  const [pendingApprovals, setPendingApprovals] = useState<RemediationAction[]>([]);
  const [remediations, setRemediations] = useState<RemediationAction[]>([]);
  const [auditEvents, setAuditEvents] = useState<AuditEvent[]>([]);
  const [dataSources, setDataSources] = useState<DataSourceHealth[]>([]);
  const [policies, setPolicies] = useState<RolePolicy[]>([]);

  // Selected issue for detail modal
  const [selectedIssue, setSelectedIssue] = useState<ReconciliationIssue | null>(null);

  const loadAllData = async () => {
    try {
      const [sumRes, issuesRes, approvalsRes, remediationsRes, auditRes, healthRes, policiesRes] = await Promise.all([
        api.getDashboardSummary().catch(() => null),
        api.getIssues(engineType).catch(() => []),
        api.getPendingApprovals().catch(() => []),
        api.getAllRemediations().catch(() => []),
        api.getAuditTrail().catch(() => []),
        api.getDataSourceHealth().catch(() => []),
        api.getPolicies().catch(() => []),
      ]);

      if (sumRes) setSummary(sumRes);
      setIssues(issuesRes);
      setPendingApprovals(approvalsRes);
      setRemediations(remediationsRes);
      setAuditEvents(auditRes);
      setDataSources(healthRes);
      setPolicies(policiesRes);
    } catch (err) {
      console.error('Failed loading app data', err);
    }
  };

  useEffect(() => {
    loadAllData();
  }, [engineType]);

  const handleRunReconciliation = async () => {
    try {
      setIsReconciling(true);
      await api.runReconciliation(engineType);
      await loadAllData();
    } catch (err: any) {
      alert(`Reconciliation error: ${err.message}`);
    } finally {
      setIsReconciling(false);
    }
  };

  const handleApprovalDecision = async (
    remediationId: string,
    reviewerId: string,
    decision: 'APPROVE' | 'REJECT',
    comment: string
  ) => {
    await api.processApprovalDecision(remediationId, reviewerId, decision, comment);
    await loadAllData();
  };

  const handleRollback = async (remediationId: string, actor: string, reason: string) => {
    await api.rollbackRemediation(remediationId, actor, reason);
    await loadAllData();
  };

  const handleUpdateHealth = async (sourceName: string, status: HealthState, freshness?: string) => {
    await api.updateDataSourceHealth(sourceName, status, freshness);
    await loadAllData();
  };

  const handleSavePolicy = async (policy: RolePolicy) => {
    await api.savePolicy(policy);
    await loadAllData();
  };

  const handleDeletePolicy = async (id: number) => {
    await api.deletePolicy(id);
    await loadAllData();
  };

  return (
    <div className="app-container">
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        pendingApprovalsCount={pendingApprovals.length}
      />

      <div className="main-content">
        <Navbar
          onRunReconciliation={handleRunReconciliation}
          isReconciling={isReconciling}
          dataSources={dataSources}
        />

        <DataSourceHealthBanner
          dataSources={dataSources}
          onNavigateToSources={() => setActiveTab('sources')}
        />

        <main className="page-body">
          {activeTab === 'dashboard' && (
            <Dashboard summary={summary} onNavigate={setActiveTab} />
          )}

          {activeTab === 'reconciliation' && (
            <ReconciliationPage
              issues={issues}
              engineType={engineType}
              onToggleEngine={(eng) => setEngineType(eng)}
              onSelectIssue={setSelectedIssue}
            />
          )}

          {activeTab === 'approvals' && (
            <ApprovalQueuePage
              pendingApprovals={pendingApprovals}
              onDecision={handleApprovalDecision}
            />
          )}

          {activeTab === 'rollback' && (
            <RemediationRollbackPage
              remediations={remediations}
              onRollback={handleRollback}
            />
          )}

          {activeTab === 'audit' && (
            <AuditTrailPage auditEvents={auditEvents} />
          )}

          {activeTab === 'sources' && (
            <DataSourceHealthPage
              dataSources={dataSources}
              onUpdateHealth={handleUpdateHealth}
              onRunReconciliation={handleRunReconciliation}
            />
          )}

          {activeTab === 'policies' && (
            <PolicyManagementPage
              policies={policies}
              onSavePolicy={handleSavePolicy}
              onDeletePolicy={handleDeletePolicy}
            />
          )}

          {activeTab === 'evaluation' && <EvaluationMetricsPage />}
        </main>
      </div>

      {/* Evidence Detail Modal */}
      {selectedIssue && (
        <IssueDetailPage
          issue={selectedIssue}
          onClose={() => setSelectedIssue(null)}
          onNavigateToApproval={() => setActiveTab('approvals')}
        />
      )}
    </div>
  );
};
