import React from 'react';
import { DataSourceHealth } from '../types';
import { Database, ShieldCheck, AlertCircle, Clock } from 'lucide-react';

interface Props {
  dataSources: DataSourceHealth[];
  onNavigateToSources: () => void;
}

export const DataSourceHealthBanner: React.FC<Props> = ({ dataSources, onNavigateToSources }) => {
  if (!dataSources || dataSources.length === 0) return null;

  return (
    <div style={{
      background: 'rgba(17, 24, 39, 0.9)',
      borderBottom: '1px solid var(--border-color)',
      padding: '0.625rem 2rem',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      fontSize: '0.8rem',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)' }}>
        <Database size={15} color="var(--accent-cyan)" />
        <span style={{ fontWeight: 600 }}>Active Data Sources & Health Status:</span>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
        {dataSources.map((source) => {
          let badgeClass = 'badge-health-AVAILABLE';
          if (source.status === 'STALE') badgeClass = 'badge-health-STALE';
          if (source.status === 'DELAYED') badgeClass = 'badge-health-DELAYED';
          if (source.status === 'UNAVAILABLE') badgeClass = 'badge-health-UNAVAILABLE';

          return (
            <div key={source.sourceName} style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
              <span style={{ color: 'var(--text-muted)', fontWeight: 500 }}>{source.sourceName}:</span>
              <span className={`badge ${badgeClass}`} style={{ fontSize: '0.68rem', padding: '0.1rem 0.4rem' }}>
                {source.status}
              </span>
            </div>
          );
        })}
      </div>

      <button
        onClick={onNavigateToSources}
        style={{
          background: 'transparent',
          border: 'none',
          color: 'var(--accent-blue)',
          fontSize: '0.75rem',
          fontWeight: 600,
          cursor: 'pointer',
          display: 'flex',
          alignItems: 'center',
          gap: '0.25rem',
        }}
      >
        Simulate Outage / Manage →
      </button>
    </div>
  );
};
