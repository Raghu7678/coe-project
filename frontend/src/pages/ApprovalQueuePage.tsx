import React, { useState } from 'react';
import { RemediationAction } from '../types';
import { CheckSquare, CheckCircle, XCircle, UserCheck, ShieldAlert, MessageSquare } from 'lucide-react';

interface Props {
  pendingApprovals: RemediationAction[];
  onDecision: (remediationId: string, reviewerId: string, decision: 'APPROVE' | 'REJECT', comment: string) => Promise<void>;
}

export const ApprovalQueuePage: React.FC<Props> = ({ pendingApprovals, onDecision }) => {
  const [reviewerId, setReviewerId] = useState('sec_reviewer');
  const [comments, setComments] = useState<Record<string, string>>({});
  const [processingId, setProcessingId] = useState<string | null>(null);

  const handleDecisionSubmit = async (remediationId: string, decision: 'APPROVE' | 'REJECT') => {
    try {
      setProcessingId(remediationId);
      const comment = comments[remediationId] || (decision === 'APPROVE' ? 'Approved based on JML policy policy mismatch' : 'Rejected by security reviewer');
      await onDecision(remediationId, reviewerId, decision, comment);
    } catch (err: any) {
      alert(`Approval Error: ${err.message}`);
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
          Accountable Approval Queue
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Review high-impact remediation requests. Enforces accountable reviewer separation before executing access changes.
        </p>
      </div>

      {/* Reviewer Identity Selector */}
      <div className="glass-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <UserCheck size={20} color="var(--accent-blue)" />
          <div>
            <div style={{ fontSize: '0.85rem', fontWeight: 700, color: 'white' }}>Accountable Reviewer Identity</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Self-approval rule: Reviewer must differ from requester</div>
          </div>
        </div>
        <input
          type="text"
          value={reviewerId}
          onChange={(e) => setReviewerId(e.target.value)}
          placeholder="Enter reviewer ID (e.g. sec_reviewer)"
          style={{
            padding: '0.5rem 0.875rem',
            background: 'var(--bg-secondary)',
            border: '1px solid var(--border-color)',
            borderRadius: '8px',
            color: 'white',
            fontWeight: 600,
            fontSize: '0.85rem',
          }}
        />
      </div>

      {/* Pending Items List */}
      {pendingApprovals.length === 0 ? (
        <div className="glass-card" style={{ textAlign: 'center', padding: '3rem 1.5rem', color: 'var(--text-muted)' }}>
          <CheckCircle size={40} color="var(--accent-emerald)" style={{ marginBottom: '0.75rem' }} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white' }}>No Pending Approvals</h3>
          <p style={{ fontSize: '0.85rem' }}>All high-impact remediation actions have been reviewed!</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {pendingApprovals.map((item) => (
            <div key={item.remediationId} className="glass-card" style={{ borderLeft: '4px solid var(--risk-high)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <span className="badge badge-risk-HIGH">PENDING REVIEW</span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>ID: {item.remediationId}</span>
                  </div>
                  <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white' }}>
                    {item.actionType.replace(/_/g, ' ')} for {item.userId} on {item.applicationName}
                  </h3>
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textAlign: 'right' }}>
                  Requested by: <strong style={{ color: 'var(--text-primary)' }}>{item.requestedBy}</strong><br />
                  Created: {new Date(item.createdAt).toLocaleString()}
                </div>
              </div>

              {/* State Comparison Box */}
              <div style={{ padding: '0.75rem 1rem', background: 'rgba(0,0,0,0.25)', borderRadius: '8px', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '1.5rem', fontSize: '0.85rem' }}>
                <div>
                  <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>PREVIOUS STATE</span>
                  <div style={{ fontWeight: 700, color: '#f87171' }}>{item.previousState}</div>
                </div>
                <span>→</span>
                <div>
                  <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>PROPOSED STATE</span>
                  <div style={{ fontWeight: 700, color: '#34d399' }}>{item.proposedState}</div>
                </div>
              </div>

              {/* Reviewer Comment & Action Controls */}
              <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
                <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
                  <MessageSquare size={15} color="var(--text-muted)" style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }} />
                  <input
                    type="text"
                    placeholder="Enter review decision rationale/comment..."
                    value={comments[item.remediationId] || ''}
                    onChange={(e) => setComments({ ...comments, [item.remediationId]: e.target.value })}
                    style={{
                      width: '100%',
                      padding: '0.45rem 0.75rem 0.45rem 2.25rem',
                      background: 'rgba(255, 255, 255, 0.04)',
                      border: '1px solid var(--border-color)',
                      borderRadius: '6px',
                      color: 'white',
                      fontSize: '0.85rem',
                    }}
                  />
                </div>

                <div style={{ display: 'flex', gap: '0.625rem' }}>
                  <button
                    onClick={() => handleDecisionSubmit(item.remediationId, 'APPROVE')}
                    disabled={processingId === item.remediationId}
                    className="btn btn-success"
                  >
                    <CheckCircle size={16} /> Approve & Execute
                  </button>
                  <button
                    onClick={() => handleDecisionSubmit(item.remediationId, 'REJECT')}
                    disabled={processingId === item.remediationId}
                    className="btn btn-danger"
                  >
                    <XCircle size={16} /> Reject
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
