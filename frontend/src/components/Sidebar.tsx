import React from 'react';
import {
  LayoutDashboard,
  ShieldAlert,
  CheckSquare,
  RotateCcw,
  History,
  Activity,
  FileCode,
  BarChart3,
  Users,
} from 'lucide-react';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  pendingApprovalsCount: number;
}

export const Sidebar: React.FC<SidebarProps> = ({ activeTab, setActiveTab, pendingApprovalsCount }) => {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'reconciliation', label: 'Reconciliation', icon: ShieldAlert },
    { id: 'approvals', label: 'Approval Queue', icon: CheckSquare, badge: pendingApprovalsCount },
    { id: 'rollback', label: 'Remediation & Rollback', icon: RotateCcw },
    { id: 'audit', label: 'Audit Trail', icon: History },
    { id: 'sources', label: 'Data Source Health', icon: Activity },
    { id: 'policies', label: 'Role Access Policies', icon: FileCode },
    { id: 'evaluation', label: 'Baseline vs Prototype', icon: BarChart3 },
  ];

  return (
    <aside style={{
      width: '260px',
      background: 'var(--bg-secondary)',
      borderRight: '1px solid var(--border-color)',
      display: 'flex',
      flexDirection: 'column',
      padding: '1.25rem 1rem',
      gap: '1.5rem',
      flexShrink: 0,
    }}>
      {/* Brand Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.5rem 0.5rem 1rem 0.5rem', borderBottom: '1px solid var(--border-color)' }}>
        <div style={{
          width: '38px',
          height: '38px',
          borderRadius: '10px',
          background: 'linear-gradient(135deg, #06b6d4, #3b82f6)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          boxShadow: '0 4px 12px rgba(6, 182, 212, 0.4)',
        }}>
          <ShieldAlert size={22} />
        </div>
        <div>
          <h2 style={{ fontSize: '1.1rem', fontWeight: 800, background: 'linear-gradient(90deg, #fff, #93c5fd)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            JML Engine
          </h2>
          <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', fontWeight: 600, letterSpacing: '0.05em' }}>
            ACCESS RECONCILIATION
          </span>
        </div>
      </div>

      {/* Navigation List */}
      <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.375rem' }}>
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '0.75rem 0.875rem',
                borderRadius: '8px',
                border: 'none',
                background: isActive ? 'linear-gradient(90deg, rgba(59, 130, 246, 0.2), rgba(59, 130, 246, 0.05))' : 'transparent',
                color: isActive ? '#60a5fa' : 'var(--text-secondary)',
                fontWeight: isActive ? 600 : 500,
                cursor: 'pointer',
                transition: 'var(--transition)',
                borderLeft: isActive ? '3px solid var(--accent-blue)' : '3px solid transparent',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <Icon size={18} color={isActive ? '#60a5fa' : 'var(--text-muted)'} />
                <span style={{ fontSize: '0.875rem' }}>{item.label}</span>
              </div>
              {item.badge !== undefined && item.badge > 0 && (
                <span style={{
                  background: 'var(--risk-high)',
                  color: 'white',
                  fontSize: '0.7rem',
                  fontWeight: 700,
                  padding: '0.15rem 0.45rem',
                  borderRadius: '9999px',
                }}>
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* System Status Footer */}
      <div style={{ marginTop: 'auto', padding: '0.875rem', borderRadius: '8px', background: 'rgba(255, 255, 255, 0.03)', border: '1px solid var(--border-color)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--accent-emerald)', boxShadow: '0 0 8px var(--accent-emerald)' }}></span>
          <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-primary)' }}>Engine Online</span>
        </div>
        <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>v1.0.0 • Multi-Source Mode</span>
      </div>
    </aside>
  );
};
