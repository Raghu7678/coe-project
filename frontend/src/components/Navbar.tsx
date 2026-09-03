import React from 'react';
import { UserCheck, RefreshCw, AlertTriangle } from 'lucide-react';
import { DataSourceHealth } from '../types';

interface NavbarProps {
  onRunReconciliation: () => void;
  isReconciling: boolean;
  dataSources: DataSourceHealth[];
}

export const Navbar: React.FC<NavbarProps> = ({ onRunReconciliation, isReconciling, dataSources }) => {
  const hasIssue = dataSources.some((ds) => ds.status !== 'AVAILABLE');

  return (
    <header style={{
      height: '64px',
      background: 'var(--bg-secondary)',
      borderBottom: '1px solid var(--border-color)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '0 2rem',
      position: 'sticky',
      top: 0,
      zIndex: 100,
    }}>
      {/* Search / Title Context */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <h1 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
          Access Reconciliation Console
        </h1>
        {hasIssue && (
          <span className="badge badge-health-STALE" style={{ gap: '0.25rem' }}>
            <AlertTriangle size={12} /> Source Health Alert
          </span>
        )}
      </div>

      {/* Actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
        <button
          onClick={onRunReconciliation}
          disabled={isReconciling}
          className="btn btn-primary"
          style={{ padding: '0.45rem 1rem' }}
        >
          <RefreshCw size={16} className={isReconciling ? 'animate-spin' : ''} />
          {isReconciling ? 'Running Analysis...' : 'Run Reconciliation'}
        </button>

        {/* User Identity Info */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', paddingLeft: '1rem', borderLeft: '1px solid var(--border-color)' }}>
          <div style={{
            width: '34px',
            height: '34px',
            borderRadius: '50%',
            background: 'rgba(59, 130, 246, 0.2)',
            color: '#60a5fa',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            border: '1px solid rgba(59, 130, 246, 0.4)',
          }}>
            <UserCheck size={18} />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-primary)' }}>Security Reviewer</span>
            <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>sec_reviewer@saas.com</span>
          </div>
        </div>
      </div>
    </header>
  );
};
