import React, { useState } from 'react';
import { RolePolicy, PermissionLevel } from '../types';
import { FileCode, Plus, Trash2, CheckCircle2 } from 'lucide-react';

interface Props {
  policies: RolePolicy[];
  onSavePolicy: (policy: RolePolicy) => Promise<void>;
  onDeletePolicy: (id: number) => Promise<void>;
}

export const PolicyManagementPage: React.FC<Props> = ({ policies, onSavePolicy, onDeletePolicy }) => {
  const [roleName, setRoleName] = useState('Developer');
  const [applicationName, setApplicationName] = useState('GitHub');
  const [expectedPermission, setExpectedPermission] = useState<PermissionLevel>('WRITE');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleAddPolicy = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      await onSavePolicy({ roleName, applicationName, expectedPermission, required: true });
    } catch (err: any) {
      alert(`Error saving policy: ${err.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
          Role & Access Policy Management
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Customer administrators define expected application permissions per role. The engine evaluates actual entitlements against these policies.
        </p>
      </div>

      {/* Add New Policy Form */}
      <form onSubmit={handleAddPolicy} className="glass-card" style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
        <div style={{ flex: 1, minWidth: '180px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '0.35rem' }}>
            ROLE NAME
          </label>
          <input
            type="text"
            placeholder="e.g. Developer"
            value={roleName}
            onChange={(e) => setRoleName(e.target.value)}
            required
            style={{ width: '100%', padding: '0.5rem 0.75rem', background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'white' }}
          />
        </div>

        <div style={{ flex: 1, minWidth: '180px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '0.35rem' }}>
            SAAS APPLICATION
          </label>
          <input
            type="text"
            placeholder="e.g. GitHub"
            value={applicationName}
            onChange={(e) => setApplicationName(e.target.value)}
            required
            style={{ width: '100%', padding: '0.5rem 0.75rem', background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'white' }}
          />
        </div>

        <div style={{ flex: 1, minWidth: '180px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '0.35rem' }}>
            EXPECTED PERMISSION
          </label>
          <select
            value={expectedPermission}
            onChange={(e) => setExpectedPermission(e.target.value as PermissionLevel)}
            style={{ width: '100%', padding: '0.5rem 0.75rem', background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'white' }}
          >
            <option value="READ">READ</option>
            <option value="USER">USER</option>
            <option value="WRITE">WRITE</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>

        <button type="submit" disabled={isSubmitting} className="btn btn-primary">
          <Plus size={16} /> Save Policy Rule
        </button>
      </form>

      {/* Existing Policies Table */}
      <div className="data-table-wrapper">
        <table className="data-table">
          <thead>
            <tr>
              <th>ROLE NAME</th>
              <th>APPLICATION</th>
              <th>EXPECTED PERMISSION RANK</th>
              <th>STATUS</th>
              <th>ACTION</th>
            </tr>
          </thead>
          <tbody>
            {policies.map((p) => (
              <tr key={p.id || `${p.roleName}-${p.applicationName}`}>
                <td style={{ fontWeight: 700, color: 'white' }}>{p.roleName}</td>
                <td style={{ fontWeight: 600, color: 'var(--accent-cyan)' }}>{p.applicationName}</td>
                <td>
                  <span style={{ fontWeight: 800, color: p.expectedPermission === 'ADMIN' ? 'var(--risk-critical)' : 'var(--accent-blue)' }}>
                    {p.expectedPermission}
                  </span>
                </td>
                <td>
                  <span style={{ fontSize: '0.75rem', color: '#34d399', fontWeight: 600 }}>Active Rule</span>
                </td>
                <td>
                  {p.id && (
                    <button onClick={() => onDeletePolicy(p.id!)} className="btn btn-danger" style={{ padding: '0.3rem 0.5rem', fontSize: '0.75rem' }}>
                      <Trash2 size={13} />
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
