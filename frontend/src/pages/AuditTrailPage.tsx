import React, { useState } from 'react';
import { AuditEvent } from '../types';
import { History, Search, Filter, ShieldCheck, ArrowRight } from 'lucide-react';

interface Props {
  auditEvents: AuditEvent[];
}

export const AuditTrailPage: React.FC<Props> = ({ auditEvents }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedEventType, setSelectedEventType] = useState('ALL');

  const filteredEvents = auditEvents.filter((event) => {
    const matchesSearch =
      event.userId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      event.actor.toLowerCase().includes(searchTerm.toLowerCase()) ||
      event.action.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (event.reason && event.reason.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesType = selectedEventType === 'ALL' || event.eventType === selectedEventType;

    return matchesSearch && matchesType;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
          Immutable Audit Trail
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Complete tamper-evident log of all reconciliation runs, issue detections, approval decisions, access removals, and rollbacks.
        </p>
      </div>

      {/* Filter Controls */}
      <div className="glass-card" style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
        <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '0.875rem', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            placeholder="Search by user ID, actor, action, or reason..."
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

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Filter size={15} color="var(--text-muted)" />
          <select
            value={selectedEventType}
            onChange={(e) => setSelectedEventType(e.target.value)}
            style={{
              padding: '0.5rem 0.875rem',
              background: 'var(--bg-secondary)',
              border: '1px solid var(--border-color)',
              borderRadius: '8px',
              color: 'white',
              fontSize: '0.85rem',
            }}
          >
            <option value="ALL">All Event Types</option>
            <option value="RECONCILIATION_STARTED">Reconciliation Started</option>
            <option value="ISSUE_DETECTED">Issue Detected</option>
            <option value="APPROVAL_GRANTED">Approval Granted</option>
            <option value="APPROVAL_REJECTED">Approval Rejected</option>
            <option value="ACCESS_REMOVED">Access Removed</option>
            <option value="ACCESS_REDUCED">Access Reduced</option>
            <option value="ROLLBACK_EXECUTED">Rollback Executed</option>
            <option value="DATA_SOURCE_HEALTH_CHANGED">Health Changed</option>
          </select>
        </div>
      </div>

      {/* Timeline List */}
      <div className="data-table-wrapper">
        <table className="data-table">
          <thead>
            <tr>
              <th>TIMESTAMP</th>
              <th>EVENT TYPE</th>
              <th>AFFECTED USER</th>
              <th>ACTOR</th>
              <th>ACTION</th>
              <th>STATE CHANGE & REASON</th>
              <th>DATA SOURCES</th>
            </tr>
          </thead>
          <tbody>
            {filteredEvents.length === 0 ? (
              <tr>
                <td colSpan={7} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                  No audit events found.
                </td>
              </tr>
            ) : (
              filteredEvents.map((event) => (
                <tr key={event.auditId}>
                  <td style={{ fontSize: '0.78rem', color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>
                    {new Date(event.timestamp).toLocaleString()}
                  </td>
                  <td>
                    <span style={{
                      fontSize: '0.7rem',
                      fontWeight: 700,
                      padding: '0.2rem 0.5rem',
                      borderRadius: '4px',
                      background: event.eventType === 'ROLLBACK_EXECUTED' ? 'rgba(249, 115, 22, 0.2)' : (event.eventType === 'ACCESS_REMOVED' || event.eventType === 'ACCESS_REDUCED' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(59, 130, 246, 0.15)'),
                      color: event.eventType === 'ROLLBACK_EXECUTED' ? 'var(--risk-high)' : (event.eventType === 'ACCESS_REMOVED' || event.eventType === 'ACCESS_REDUCED' ? '#34d399' : '#60a5fa'),
                    }}>
                      {event.eventType}
                    </span>
                  </td>
                  <td style={{ fontWeight: 600, color: 'white' }}>{event.userId}</td>
                  <td style={{ fontWeight: 600, color: 'var(--accent-cyan)' }}>{event.actor}</td>
                  <td style={{ fontSize: '0.85rem' }}>{event.action}</td>
                  <td>
                    <div style={{ fontSize: '0.8rem' }}>
                      {event.previousState && event.newState && (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', marginBottom: '0.2rem' }}>
                          <span style={{ color: '#f87171' }}>{event.previousState}</span>
                          <span>→</span>
                          <span style={{ color: '#34d399' }}>{event.newState}</span>
                        </div>
                      )}
                      {event.reason && <div style={{ color: 'var(--text-secondary)', fontStyle: 'italic' }}>"{event.reason}"</div>}
                    </div>
                  </td>
                  <td style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    {event.dataSourcesUsed || 'N/A'}
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
