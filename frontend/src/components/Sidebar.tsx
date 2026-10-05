import React from 'react';
import {
  LayoutDashboard,
  HardHat,
  CheckSquare,
  RefreshCw,
  History,
  Activity,
  Sliders,
  BarChart3
} from 'lucide-react';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  pendingApprovalsCount?: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  setActiveTab,
  pendingApprovalsCount = 0
}) => {
  const menuItems = [
    { id: 'dashboard', label: 'Executive Dashboard', icon: LayoutDashboard },
    { id: 'assessment', label: 'Risk Evaluation Hub', icon: HardHat },
    { id: 'approvals', label: 'Approval Queue', icon: CheckSquare, badge: pendingApprovalsCount },
    { id: 'remediation', label: 'Remediation & Rollback', icon: RefreshCw },
    { id: 'audit', label: 'Immutable Audit Trail', icon: History },
    { id: 'sources', label: 'Data Feeds Health', icon: Activity },
    { id: 'policy', label: 'Policy Manager', icon: Sliders },
    { id: 'evaluation', label: 'Baseline vs Prototype', icon: BarChart3 },
  ];

  return (
    <aside className="w-64 border-r border-cyan-900/40 bg-slate-950/90 flex flex-col justify-between p-4 sticky top-16 h-[calc(100vh-4rem)]">
      <div className="space-y-1">
        <div className="px-3 py-2 text-[10px] font-mono tracking-wider text-slate-500 uppercase">
          NAVIGATION MENU
        </div>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
                isActive
                  ? 'bg-gradient-to-r from-cyan-950/80 to-blue-950/80 border border-cyan-500/40 text-cyan-300 shadow-md shadow-cyan-950/50'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/60 border border-transparent'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Icon className={`w-4 h-4 ${isActive ? 'text-cyan-400' : 'text-slate-500'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge !== undefined && item.badge > 0 && (
                <span className="px-2 py-0.5 text-xs font-mono font-bold rounded-full bg-amber-500/20 text-amber-400 border border-amber-500/40 animate-pulse">
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>

      <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 text-xs font-mono space-y-1 text-slate-400">
        <div className="flex justify-between text-slate-300 font-semibold">
          <span>Target SLA</span>
          <span className="text-cyan-400">15m - 120m</span>
        </div>
        <div className="flex justify-between">
          <span>Engine</span>
          <span className="text-emerald-400">PROTOTYPE v2.4</span>
        </div>
      </div>
    </aside>
  );
};
