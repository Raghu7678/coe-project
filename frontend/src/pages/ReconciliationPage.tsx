import React, { useState } from 'react';
import { ReconciliationIssue } from '../types';
import { ShieldAlert, Search, Filter, Eye, Layers, Zap, AlertTriangle } from 'lucide-react';

interface Props {
  issues: ReconciliationIssue[];
  engineType: 'PROTOTYPE' | 'BASELINE';
  onToggleEngine: (engine: 'PROTOTYPE' | 'BASELINE') => void;
  onSelectIssue: (issue: ReconciliationIssue) => void;
}

export const ReconciliationPage: React.FC<Props> = ({
  issues,
  engineType,
  onToggleEngine,
  onSelectIssue,
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRisk, setSelectedRisk] = useState('ALL');
  const [selectedType, setSelectedType] = useState('ALL');

  const filteredIssues = issues.filter((issue) => {
    const matchesSearch =
      issue.employeeName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      issue.userId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      issue.applicationName.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesRisk = selectedRisk === 'ALL' || issue.riskLevel === selectedRisk;
    const matchesType = selectedType === 'ALL' || issue.issueType === selectedType;

    return matchesSearch && matchesRisk && matchesType;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header & Engine Selector */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
            Reconciliation Results
          </h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
            Detected access anomalies, risk classification, data source confidence scores, and remediation recommendations.
          </p>
        </div>

        {/* Engine Switcher Toggle */}
        <div style={{
          display: 'flex',
          background: 'var(--bg-secondary)',
          padding: '0.25rem',
          borderRadius: '10px',
          border: '1px solid var(--border-color)',
        }}>
          <button
            onClick={() => onToggleEngine('PROTOTYPE')}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              background: engineType === 'PROTOTYPE' ? 'var(--accent-blue)' : 'transparent',
              color: engineType === 'PROTOTYPE' ? 'white' : 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
            }}
          >
            <Zap size={15} /> Prototype (Multi-Source)
          </button>
          <button
            onClick={() => onToggleEngine('BASELINE')}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              background: engineType === 'BASELINE' ? 'rgba(255, 255, 255, 0.15)' : 'transparent',
              color: engineType === 'BASELINE' ? 'white' : 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
            }}
          >
            <Layers size={15} /> Baseline (Naive HR+App)
          </button>
        </div>
      </div>

      {/* Engine Alert Banner */}
      {engineType === 'BASELINE' ? (
        <div style={{ padding: '0.75rem 1rem', background: 'rgba(234, 179, 8, 0.1)', border: '1px solid rgba(234, 179, 8, 0.3)', borderRadius: '8px', color: '#fde047', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <AlertTriangle size={16} />
          <strong>Baseline Engine Active:</strong> Uses ONLY HR role and application entitlements. Does NOT check directory groups, approval history, or data health.
        </div>
      ) : (
        <div style={{ padding: '0.75rem 1rem', background: 'rgba(6, 182, 212, 0.1)', border: '1px solid rgba(6, 182, 212, 0.3)', borderRadius: '8px', color: '#67e8f9', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Zap size={16} />
          <strong>Prototype Resilient Engine Active:</strong> Integrates HR + Directory + Entitlements + Approvals + Confidence + Risk + Accountable Approvals + Rollback.
        </div>
      )}

      {/* Filter Controls */}
      <div className="glass-card" style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
        {/* Search */}
        <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '0.875rem', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            placeholder="Search employee, user ID, or application..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: '100%',
              padding: '0.5rem 0.875rem 0.5rem 2.5rem',
              background: 'rgba(255, 255, 255, 0.04)',
              border: '1px solid var(--border-color)',
              borderRadius: '8px',
              color: 'white',
              fontSize: '0.875rem',
              outline: 'none',
            }}
          />
        </div>

        {/* Risk Filter */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Filter size={15} color="var(--text-muted)" />
          <select
            value={selectedRisk}
            onChange={(e) => setSelectedRisk(e.target.value)}
            style={{
              padding: '0.5rem 0.875rem',
              background: 'var(--bg-secondary)',
              border: '1px solid var(--border-color)',
              borderRadius: '8px',
              color: 'white',
              fontSize: '0.85rem',
            }}
          >
            <option value="ALL">All Risk Levels</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>

        {/* Issue Type Filter */}
        <select
          value={selectedType}
          onChange={(e) => setSelectedType(e.target.value)}
          style={{
            padding: '0.5rem 0.875rem',
            background: 'var(--bg-secondary)',
            border: '1px solid var(--border-color)',
            borderRadius: '8px',
            color: 'white',
            fontSize: '0.85rem',
          }}
        >
          <option value="ALL">All Issue Types</option>
          <option value="ORPHANED_ACCESS">Orphaned Access</option>
          <option value="EXCESSIVE_ACCESS">Excessive Access</option>
          <option value="UNAPPROVED_ACCESS">Unapproved Access</option>
          <option value="MISSING_ACCESS">Missing Access</option>
          <option value="ROLE_CHANGE_ACCESS_CONFLICT">Role Change Conflict</option>
        </select>
      </div>

      {/* Table */}
      <div className="data-table-wrapper">
        <table className="data-table">
          <thead>
            <tr>
              <th>USER / EMPLOYEE</th>
              <th>CURRENT ROLE</th>
              <th>ISSUE TYPE</th>
              <th>APPLICATION</th>
              <th>EXPECTED vs ACTUAL</th>
              <th>RISK</th>
              <th>CONFIDENCE</th>
              <th>STATUS</th>
              <th>ACTION</th>
            </tr>
          </thead>
          <tbody>
            {filteredIssues.length === 0 ? (
              <tr>
                <td colSpan={9} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                  No reconciliation issues match the selected criteria.
                </td>
              </tr>
            ) : (
              filteredIssues.map((issue) => (
                <tr key={issue.issueId}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{issue.employeeName}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{issue.userId}</div>
                  </td>
                  <td style={{ fontSize: '0.85rem' }}>{issue.currentRole}</td>
                  <td>
                    <span style={{ fontWeight: 600, fontSize: '0.8rem', color: 'var(--text-primary)' }}>
                      {issue.issueType.replace(/_/g, ' ')}
                    </span>
                  </td>
                  <td style={{ fontWeight: 600, color: 'var(--accent-cyan)' }}>{issue.applicationName}</td>
                  <td>
                    <div style={{ fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
                      <span style={{ color: 'var(--text-muted)' }}>Exp: {issue.expectedAccess || 'NONE'}</span>
                      <span>→</span>
                      <span style={{ color: '#f87171', fontWeight: 700 }}>Act: {issue.actualAccess || 'NONE'}</span>
                    </div>
                  </td>
                  <td>
                    <span className={`badge badge-risk-${issue.riskLevel}`}>
                      {issue.riskLevel}
                    </span>
                  </td>
                  <td>
                    <span style={{
                      fontWeight: 700,
                      fontSize: '0.85rem',
                      color: issue.confidenceScore >= 0.8 ? '#34d399' : (issue.confidenceScore >= 0.5 ? '#fde047' : '#f87171')
                    }}>
                      {(issue.confidenceScore * 100).toFixed(0)}%
                    </span>
                  </td>
                  <td>
                    <span style={{
                      fontSize: '0.75rem',
                      fontWeight: 600,
                      padding: '0.2rem 0.5rem',
                      borderRadius: '4px',
                      background: issue.status === 'RESOLVED' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(255, 255, 255, 0.05)',
                      color: issue.status === 'RESOLVED' ? '#34d399' : 'var(--text-secondary)'
                    }}>
                      {issue.status}
                    </span>
                  </td>
                  <td>
                    <button
                      onClick={() => onSelectIssue(issue)}
                      className="btn btn-secondary"
                      style={{ padding: '0.35rem 0.65rem', fontSize: '0.75rem', gap: '0.25rem' }}
                    >
                      <Eye size={13} /> Evidence
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
