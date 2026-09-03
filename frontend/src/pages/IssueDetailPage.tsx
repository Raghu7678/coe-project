import React from 'react';
import { ReconciliationIssue } from '../types';
import { X, ShieldAlert, CheckCircle, AlertTriangle, Database, FileText, User, ArrowRight, Lock } from 'lucide-react';

interface Props {
  issue: ReconciliationIssue | null;
  onClose: () => void;
  onNavigateToApproval: () => void;
}

export const IssueDetailPage: React.FC<Props> = ({ issue, onClose, onNavigateToApproval }) => {
  if (!issue) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border-color)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              background: issue.riskLevel === 'CRITICAL' ? 'rgba(239, 68, 68, 0.2)' : 'rgba(249, 115, 22, 0.2)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: issue.riskLevel === 'CRITICAL' ? 'var(--risk-critical)' : 'var(--risk-high)',
            }}>
              <ShieldAlert size={22} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 800, color: 'var(--text-primary)' }}>
                Issue Root Cause & Evidence Inspection
              </h3>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                ID: {issue.issueId} • Detected at {new Date(issue.detectedAt).toLocaleString()}
              </span>
            </div>
          </div>
          <button onClick={onClose} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
            <X size={20} />
          </button>
        </div>

        {/* User Summary Bar */}
        <div style={{ display: 'flex', gap: '1rem', padding: '1rem', background: 'rgba(255, 255, 255, 0.03)', borderRadius: '10px', marginBottom: '1.25rem', border: '1px solid var(--border-color)' }}>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>EMPLOYEE</div>
            <div style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-primary)' }}>{issue.employeeName} ({issue.userId})</div>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>CURRENT ROLE</div>
            <div style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--accent-cyan)' }}>{issue.currentRole}</div>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>RISK LEVEL</div>
            <span className={`badge badge-risk-${issue.riskLevel}`} style={{ marginTop: '0.2rem' }}>
              {issue.riskLevel}
            </span>
          </div>
        </div>

        {/* Evidence Grid */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '1.5rem' }}>
          {/* Entitlement Diff */}
          <div style={{ padding: '1rem', background: 'var(--bg-card)', borderRadius: '10px', border: '1px solid var(--border-color)' }}>
            <h4 style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-secondary)', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Lock size={15} color="var(--accent-blue)" /> Access State Comparison
            </h4>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-around', padding: '0.75rem', background: 'rgba(0,0,0,0.2)', borderRadius: '8px' }}>
              <div style={{ textAlign: 'center' }}>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>APPLICATION</span>
                <div style={{ fontWeight: 800, fontSize: '1.1rem', color: 'white' }}>{issue.applicationName}</div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>EXPECTED LEVEL</span>
                <div style={{ fontWeight: 800, fontSize: '1rem', color: '#60a5fa' }}>{issue.expectedAccess || 'NONE'}</div>
              </div>
              <ArrowRight size={20} color="var(--text-muted)" />
              <div style={{ textAlign: 'center' }}>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>ACTUAL PERMISSION</span>
                <div style={{ fontWeight: 800, fontSize: '1rem', color: '#f87171' }}>{issue.actualAccess || 'NONE'}</div>
              </div>
            </div>
          </div>

          {/* Confidence Score Analysis */}
          <div style={{ padding: '1rem', background: 'var(--bg-card)', borderRadius: '10px', border: '1px solid var(--border-color)' }}>
            <h4 style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-secondary)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Database size={15} color="var(--accent-cyan)" /> Data Source Confidence Score
            </h4>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: issue.confidenceScore >= 0.75 ? '#34d399' : '#f87171' }}>
                {(issue.confidenceScore * 100).toFixed(0)}%
              </div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                {issue.confidenceScore >= 0.75
                  ? 'High evidence confidence. Multi-source validation confirmed with available fresh data sources.'
                  : 'Reduced confidence due to stale or missing directory/approval data sources. Auto-remediation safety gate active.'}
              </p>
            </div>
          </div>

          {/* Recommended Remediation Action */}
          <div style={{ padding: '1rem', background: 'rgba(59, 130, 246, 0.08)', borderRadius: '10px', border: '1px solid rgba(59, 130, 246, 0.3)' }}>
            <h4 style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--accent-blue)', marginBottom: '0.35rem' }}>
              RECOMMENDED REMEDIATION ACTION
            </h4>
            <div style={{ fontSize: '1rem', fontWeight: 800, color: 'white' }}>
              {issue.recommendedAction.replace(/_/g, ' ')}
            </div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              SLA Target Time: {issue.targetTimeMinutes || 60} minutes
            </span>
          </div>
        </div>

        {/* Footer Actions */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
          <button onClick={onClose} className="btn btn-secondary">Close</button>
          <button onClick={() => { onClose(); onNavigateToApproval(); }} className="btn btn-primary">
            Proceed to Approval Queue →
          </button>
        </div>
      </div>
    </div>
  );
};
