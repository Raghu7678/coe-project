import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { DataSourceHealthBanner } from './components/DataSourceHealthBanner';

import { Dashboard } from './pages/Dashboard';
import { RiskAssessmentPage } from './pages/RiskAssessmentPage';
import { AnomalyDetailPage } from './pages/AnomalyDetailPage';
import { ApprovalQueuePage } from './pages/ApprovalQueuePage';
import { QualificationRemediationPage } from './pages/QualificationRemediationPage';
import { AuditTrailPage } from './pages/AuditTrailPage';
import { DataSourceHealthPage } from './pages/DataSourceHealthPage';
import { PolicyManagementPage } from './pages/PolicyManagementPage';
import { EvaluationMetricsPage } from './pages/EvaluationMetricsPage';

import { DashboardSummary, DataSourceHealth } from './types';
import { api } from './services/api';

export function App() {
  const [activeTab, setActiveTab] = useState<string>('dashboard');
  const [selectedAnomalyId, setSelectedAnomalyId] = useState<number | null>(null);
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [healthList, setHealthList] = useState<DataSourceHealth[]>([]);

  const refreshGlobalState = async () => {
    try {
      const [sumRes, healthRes] = await Promise.all([
        api.getDashboardSummary(),
        api.getDataSourceHealth()
      ]);
      setSummary(sumRes);
      setHealthList(healthRes);
    } catch (err) {
      console.error('API Sync Error:', err);
    }
  };

  useEffect(() => {
    refreshGlobalState();
    const interval = setInterval(refreshGlobalState, 10000);
    return () => clearInterval(interval);
  }, []);

  const handleSelectAnomaly = (id: number) => {
    setSelectedAnomalyId(id);
    setActiveTab('anomaly-detail');
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-cyan-500 selection:text-slate-950">
      <Navbar
        averageConfidence={summary?.averageDataConfidence || 1.0}
        healthMap={summary?.dataSourceHealthMap}
      />

      <div className="flex flex-1">
        <Sidebar
          activeTab={activeTab}
          setActiveTab={(tab) => {
            setActiveTab(tab);
            if (tab !== 'anomaly-detail') setSelectedAnomalyId(null);
          }}
          pendingApprovalsCount={summary?.pendingApprovalsCount || 0}
        />

        <main className="flex-1 p-8 overflow-y-auto max-w-7xl">
          <DataSourceHealthBanner healthList={healthList} />

          {activeTab === 'dashboard' && (
            <Dashboard summary={summary} onNavigate={(tab) => setActiveTab(tab)} />
          )}

          {activeTab === 'assessment' && (
            <RiskAssessmentPage onSelectAnomaly={handleSelectAnomaly} />
          )}

          {activeTab === 'anomaly-detail' && selectedAnomalyId !== null && (
            <AnomalyDetailPage
              anomalyId={selectedAnomalyId}
              onBack={() => setActiveTab('assessment')}
            />
          )}

          {activeTab === 'approvals' && <ApprovalQueuePage />}

          {activeTab === 'remediation' && <QualificationRemediationPage />}

          {activeTab === 'audit' && <AuditTrailPage />}

          {activeTab === 'sources' && <DataSourceHealthPage />}

          {activeTab === 'policy' && <PolicyManagementPage />}

          {activeTab === 'evaluation' && <EvaluationMetricsPage />}
        </main>
      </div>
    </div>
  );
}

export default App;
