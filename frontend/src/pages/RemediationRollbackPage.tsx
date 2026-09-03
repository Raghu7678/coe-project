import React, { useState } from 'react';
import { RemediationAction } from '../types';
import { RotateCcw, ShieldCheck, CheckCircle2, History, X } from 'lucide-react';

interface Props {
  remediations: RemediationAction[];
  onRollback: (remediationId: string, actor: string, reason: string) => Promise<void>;
}

export const RemediationRollbackPage: React.FC<Props> = ({ remediations, onRollback }) => {
  const [selectedForRollback, setSelectedForRollback] = useState<RemediationAction | null>(null);
  const [rollbackActor, setRollbackActor] = useState('sec_admin');
  const [rollbackReason, setRollbackReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleRollbackSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedForRollback || !rollbackReason.trim()) return;

    try {
      setIsSubmitting(true);
      await onRollback(selectedForRollback.remediationId, rollbackActor, rollbackReason);
      setSelectedForRollback(null);
      setRollbackReason('');
    } catch (err: any) {
      alert(`Rollback failed: ${err.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
          Remediation History & Rollback Controls
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Inspect executed access changes and safely trigger rollbacks while maintaining immutable audit history.
        </p>
      </div>

      {/* Remediations Table */}
      <div className="data-table-wrapper">
        <table className="data-table">
          <thead>
            <tr>
              <th>REMEDIATION ID</th>
              <th>USER</th>
              <th>APPLICATION</th>
              <th>ACTION TYPE</th>
              <th>PREVIOUS → PROPOSED</th>
              <th>STATUS</th>
              <th>EXECUTED BY / DATE</th>
              <th>ROLLBACK CONTROL</th>
            </tr>
          </thead>
          <tbody>
            {remediations.length === 0 ? (
              <tr>
                <td colSpan={8} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                  No remediation actions executed yet.
                </td>
              </tr>
            ) : (
              remediations.map((action) => (
                <tr key={action.remediationId}>
                  <td style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)' }}>{action.remediationId}</td>
                  <td style={{ fontWeight: 600, color: 'white' }}>{action.userId}</td>
                  <td style={{ fontWeight: 600, color: 'var(--accent-cyan)' }}>{action.applicationName}</td>
                  <td style={{ fontSize: '0.8rem' }}>{action.actionType.replace(/_/g, ' ')}</td>
                  <td>
                    <div style={{ fontSize: '0.75rem' }}>
                      <span style={{ color: '#f87171' }}>{action.previousState}</span>
                      <span style={{ margin: '0 0.25rem' }}>→</span>
                      <span style={{ color: '#34d399' }}>{action.proposedState}</span>
                    </div>
                  </td>
                  <td>
                    <span style={{
                      fontSize: '0.75rem',
                      fontWeight: 600,
                      padding: '0.2rem 0.5rem',
                      borderRadius: '4px',
                      background: action.status === 'ROLLED_BACK' ? 'rgba(249, 115, 22, 0.2)' : (action.status === 'EXECUTED' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(255, 255, 255, 0.05)'),
                      color: action.status === 'ROLLED_BACK' ? 'var(--risk-high)' : (action.status === 'EXECUTED' ? '#34d399' : 'var(--text-secondary)'),
                    }}>
                      {action.status}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                    {action.approvedBy || action.requestedBy}<br />
                    {action.executedAt ? new Date(action.executedAt).toLocaleString() : 'N/A'}
                  </td>
                  <td>
                    {action.status === 'EXECUTED' ? (
                      <button
                        onClick={() => setSelectedForRollback(action)}
                        className="btn btn-danger"
                        style={{ padding: '0.35rem 0.65rem', fontSize: '0.75rem', gap: '0.25rem' }}
                      >
                        <RotateCcw size={13} /> Rollback
                      </button>
                    ) : (
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {action.status === 'ROLLED_BACK' ? `Rolled back: ${action.rollbackReason}` : 'N/A'}
                      </span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Rollback Modal */}
      {selectedForRollback && (
        <div className="modal-overlay" onClick={() => setSelectedForRollback(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', paddingBottom: '0.75rem', borderBottom: '1px solid var(--border-color)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <RotateCcw size={20} color="var(--risk-high)" />
                <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'white' }}>
                  Rollback Executed Access Modification
                </h3>
              </div>
              <button onClick={() => setSelectedForRollback(null)} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleRollbackSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              <div style={{ padding: '0.875rem', background: 'rgba(249, 115, 22, 0.1)', border: '1px solid rgba(249, 115, 22, 0.3)', borderRadius: '8px', fontSize: '0.85rem' }}>
                <strong>Restoration Impact:</strong> This action will restore <strong>{selectedForRollback.userId}</strong>'s access on <strong>{selectedForRollback.applicationName}</strong> back to <strong>{selectedForRollback.previousState}</strong> and record an immutable rollback audit event.
              </div>

              <div>
                <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '0.375rem' }}>
                  Actor Initiating Rollback
                </label>
                <input
                  type="text"
                  value={rollbackActor}
                  onChange={(e) => setRollbackActor(e.target.value)}
                  required
                  style={{
                    width: '100%',
                    padding: '0.5rem 0.875rem',
                    background: 'var(--bg-secondary)',
                    border: '1px solid var(--border-color)',
                    borderRadius: '8px',
                    color: 'white',
                    fontSize: '0.875rem',
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '0.375rem' }}>
                  Rollback Justification / Reason (Required)
                </label>
                <textarea
                  rows={3}
                  placeholder="Explain why this access removal is being rolled back..."
                  value={rollbackReason}
                  onChange={(e) => setRollbackReason(e.target.value)}
                  required
                  style={{
                    width: '100%',
                    padding: '0.625rem 0.875rem',
                    background: 'var(--bg-secondary)',
                    border: '1px solid var(--border-color)',
                    borderRadius: '8px',
                    color: 'white',
                    fontSize: '0.875rem',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '0.5rem' }}>
                <button type="button" onClick={() => setSelectedForRollback(null)} className="btn btn-secondary">Cancel</button>
                <button type="submit" disabled={isSubmitting} className="btn btn-danger">
                  Confirm & Execute Rollback
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
