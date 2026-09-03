import React from 'react';
import { DataSourceHealth, HealthState } from '../types';
import { Activity, Database, AlertTriangle, CheckCircle, RefreshCw } from 'lucide-react';

interface Props {
  dataSources: DataSourceHealth[];
  onUpdateHealth: (sourceName: string, status: HealthState, freshness?: string) => Promise<void>;
  onRunReconciliation: () => void;
}

export const DataSourceHealthPage: React.FC<Props> = ({ dataSources, onUpdateHealth, onRunReconciliation }) => {
  const handleStatusChange = async (sourceName: string, status: HealthState) => {
    let freshness = 'Fresh';
    if (status === 'STALE') freshness = 'Stale (48h delay)';
    if (status === 'DELAYED') freshness = 'Delayed (Sync lag)';
    if (status === 'UNAVAILABLE') freshness = 'Service Outage / Offline';

    await onUpdateHealth(sourceName, status, freshness);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
          Data Source Health & Outage Simulator
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Manage integration endpoints and simulate delayed, stale, or unavailable data sources to test engine resilience and confidence-aware safety gates.
        </p>
      </div>

      {/* Grid of Data Source Health Controls */}
      <div className="grid-2">
        {dataSources.map((source) => (
          <div key={source.sourceName} className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Database size={18} color="var(--accent-cyan)" />
                <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'white' }}>{source.sourceName}</h3>
              </div>
              <span className={`badge badge-health-${source.status}`}>
                {source.status}
              </span>
            </div>

            <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <div>Freshness: <strong style={{ color: 'white' }}>{source.freshness}</strong></div>
              <div>Last Synced: <strong style={{ color: 'white' }}>{new Date(source.lastUpdated).toLocaleString()}</strong></div>
              <div>Cached Records: <strong style={{ color: 'white' }}>{source.recordsCount || 'Dynamic'}</strong></div>
            </div>

            {/* Simulation Status Selector */}
            <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '0.875rem' }}>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem' }}>
                SIMULATE HEALTH STATE OVERRIDE:
              </label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0.375rem' }}>
                {(['AVAILABLE', 'STALE', 'DELAYED', 'UNAVAILABLE'] as HealthState[]).map((st) => (
                  <button
                    key={st}
                    onClick={() => handleStatusChange(source.sourceName, st)}
                    style={{
                      padding: '0.35rem 0.25rem',
                      fontSize: '0.7rem',
                      fontWeight: 700,
                      borderRadius: '6px',
                      border: '1px solid var(--border-color)',
                      background: source.status === st ? 'rgba(59, 130, 246, 0.3)' : 'rgba(255, 255, 255, 0.03)',
                      color: source.status === st ? '#60a5fa' : 'var(--text-muted)',
                      cursor: 'pointer',
                    }}
                  >
                    {st}
                  </button>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Test Trigger */}
      <div className="glass-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: 'rgba(6, 182, 212, 0.05)', border: '1px solid rgba(6, 182, 212, 0.2)' }}>
        <div>
          <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--accent-cyan)' }}>
            Test Engine Data Resilience
          </h4>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
            After changing a data source status above, run reconciliation to verify confidence score reduction and safety gate behavior.
          </p>
        </div>
        <button onClick={onRunReconciliation} className="btn btn-primary">
          <RefreshCw size={15} /> Run Resilient Reconciliation
        </button>
      </div>
    </div>
  );
};
