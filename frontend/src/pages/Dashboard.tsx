import React from 'react';
import { DashboardSummary } from '../types';
import { ShieldAlert, Users, AlertTriangle, CheckCircle, Clock, ArrowRight, ShieldCheck, Zap } from 'lucide-react';

interface Props {
  summary: DashboardSummary | null;
  onNavigate: (tab: string) => void;
}

export const Dashboard: React.FC<Props> = ({ summary, onNavigate }) => {
  if (!summary) {
    return <div style={{ padding: '2rem', color: 'var(--text-muted)' }}>Loading security dashboard metrics...</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
      {/* Page Header */}
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
          Security Posture & Reconciliation Overview
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Real-time Joiner-Mover-Leaver (JML) risk monitoring, data source health, and compliance target tracking.
        </p>
      </div>

      {/* Primary Metric Cards */}
      <div className="grid-4">
        <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-blue)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', fontWeight: 600 }}>TOTAL USERS MONITORED</span>
            <Users size={18} color="var(--accent-blue)" />
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem 0', color: 'white' }}>
            {summary.totalUsers}
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--accent-emerald)' }}>✓ All HR records synced</span>
        </div>

        <div className="glass-card" style={{ borderLeft: '4px solid var(--risk-critical)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', fontWeight: 600 }}>CRITICAL RISK ISSUES</span>
            <ShieldAlert size={18} color="var(--risk-critical)" />
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem 0', color: 'var(--risk-critical)' }}>
            {summary.criticalIssues}
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--risk-critical)' }}>Requires immediate review</span>
        </div>

        <div className="glass-card" style={{ borderLeft: '4px solid var(--risk-high)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', fontWeight: 600 }}>PENDING APPROVALS</span>
            <AlertTriangle size={18} color="var(--risk-high)" />
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem 0', color: 'var(--risk-high)' }}>
            {summary.pendingApprovalsCount}
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Awaiting security reviewer</span>
        </div>

        <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-emerald)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', fontWeight: 600 }}>TARGET TIME COMPLIANCE</span>
            <Clock size={18} color="var(--accent-emerald)" />
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem 0', color: 'var(--accent-emerald)' }}>
            {summary.targetCompliancePercentage}%
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Avg removal: {summary.avgRemediationTimeMinutes}m</span>
        </div>
      </div>

      {/* Middle Section: Risk Breakdown & Quick Actions */}
      <div className="grid-2">
        {/* Issue Categorization Card */}
        <div className="glass-card">
          <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1.25rem', color: 'var(--text-primary)' }}>
            Access Risk Breakdown
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', background: 'rgba(255, 255, 255, 0.03)', borderRadius: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span className="badge badge-risk-CRITICAL">ORPHANED</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>Leaver Access Retained</span>
              </div>
              <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--risk-critical)' }}>{summary.orphanedAccessCount}</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', background: 'rgba(255, 255, 255, 0.03)', borderRadius: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span className="badge badge-risk-HIGH">EXCESSIVE</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>Privilege Mismatch</span>
              </div>
              <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--risk-high)' }}>{summary.excessiveAccessCount}</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', background: 'rgba(255, 255, 255, 0.03)', borderRadius: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span className="badge badge-risk-HIGH">UNAPPROVED</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>No Valid Approval Record</span>
              </div>
              <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--risk-high)' }}>{summary.unapprovedAccessCount}</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', background: 'rgba(255, 255, 255, 0.03)', borderRadius: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span className="badge badge-risk-LOW">MISSING</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>Required Role Access Missing</span>
              </div>
              <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--accent-blue)' }}>{summary.missingAccessCount}</span>
            </div>
          </div>
        </div>

        {/* Action Panel */}
        <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
              Reconciliation Action Center
            </h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
              Execute high-impact access removals, review pending queue, or inspect baseline vs prototype performance metrics.
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <button onClick={() => onNavigate('reconciliation')} className="btn btn-primary" style={{ justifyContent: 'space-between' }}>
                <span>Inspect All Detected Issues</span>
                <ArrowRight size={16} />
              </button>

              <button onClick={() => onNavigate('approvals')} className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>
                <span>Accountable Approval Queue ({summary.pendingApprovalsCount})</span>
                <ArrowRight size={16} />
              </button>

              <button onClick={() => onNavigate('evaluation')} className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>
                <span>Run Empirical Baseline vs Prototype Experiment</span>
                <Zap size={16} color="var(--accent-cyan)" />
              </button>
            </div>
          </div>

          <div style={{ marginTop: '1.5rem', padding: '0.75rem', borderRadius: '8px', background: 'rgba(16, 185, 129, 0.08)', border: '1px solid rgba(16, 185, 129, 0.2)', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <ShieldCheck size={20} color="var(--accent-emerald)" />
            <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
              Target Time SLA Enforced: Critical Leavers (15m), Privileged High Risk (60m), Medium Risk (24h).
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
