import React from 'react';
import {
  Users,
  ShieldCheck,
  AlertTriangle,
  Clock,
  RefreshCw,
  Activity,
  ArrowRight
} from 'lucide-react';
import { DashboardSummary } from '../types';

interface DashboardProps {
  summary: DashboardSummary | null;
  onNavigate: (tab: string) => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ summary, onNavigate }) => {
  if (!summary) {
    return (
      <div className="p-8 text-center text-slate-400 font-mono">
        Loading executive dashboard metrics...
      </div>
    );
  }

  const confidencePct = Math.round(summary.averageDataConfidence * 100);

  const kpis = [
    {
      title: 'Total Contractors',
      value: summary.totalContractors,
      icon: Users,
      color: 'from-blue-600/20 to-cyan-600/20 border-cyan-500/30 text-cyan-400',
      subtitle: 'Evaluated vendors'
    },
    {
      title: 'Fully Qualified',
      value: summary.qualifiedContractors,
      icon: ShieldCheck,
      color: 'from-emerald-600/20 to-teal-600/20 border-emerald-500/30 text-emerald-400',
      subtitle: 'Cleared & complaint'
    },
    {
      title: 'High/Critical Risk Flags',
      value: summary.highRiskAnomaliesCount,
      icon: AlertTriangle,
      color: 'from-rose-600/20 to-amber-600/20 border-rose-500/30 text-rose-400',
      subtitle: 'Action required'
    },
    {
      title: 'Pending Approvals',
      value: summary.pendingApprovalsCount,
      icon: Clock,
      color: 'from-amber-600/20 to-yellow-600/20 border-amber-500/30 text-amber-400',
      subtitle: 'Dual-control queue'
    },
    {
      title: 'Executed Remediations',
      value: summary.executedRemediationsCount,
      icon: RefreshCw,
      color: 'from-purple-600/20 to-indigo-600/20 border-purple-500/30 text-purple-400',
      subtitle: 'With rollback support'
    },
    {
      title: 'Data Feed Confidence',
      value: `${confidencePct}%`,
      icon: Activity,
      color: confidencePct >= 75 ? 'from-teal-600/20 to-emerald-600/20 border-teal-500/30 text-teal-400' : 'from-amber-600/20 to-rose-600/20 border-amber-500/30 text-amber-400',
      subtitle: 'Multi-source health'
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-wide">Executive Risk Overview</h2>
          <p className="text-sm text-slate-400">Real-time status of contractor safety, financial stability, insurance compliance, and sanctions.</p>
        </div>
        <button
          onClick={() => onNavigate('assessment')}
          className="px-4 py-2 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white text-sm font-semibold rounded-xl flex items-center space-x-2 shadow-lg shadow-cyan-900/30 transition-all"
        >
          <span>Run Pre-Qualification Assessment</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {kpis.map((kpi, idx) => {
          const Icon = kpi.icon;
          return (
            <div
              key={idx}
              className={`p-5 rounded-2xl bg-gradient-to-br ${kpi.color} border backdrop-blur-sm relative overflow-hidden group hover:scale-[1.02] transition-transform`}
            >
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-xs font-mono font-medium text-slate-400 uppercase tracking-wider">{kpi.title}</p>
                  <h3 className="text-3xl font-extrabold text-white mt-1 font-mono">{kpi.value}</h3>
                  <p className="text-xs text-slate-400 mt-1">{kpi.subtitle}</p>
                </div>
                <div className="p-3 bg-slate-950/60 rounded-xl border border-slate-800">
                  <Icon className="w-6 h-6" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="p-6 bg-slate-900/70 rounded-2xl border border-slate-800 space-y-4">
          <h3 className="text-lg font-bold text-white flex items-center space-x-2">
            <Activity className="w-5 h-5 text-cyan-400" />
            <span>Data Feeds Health Matrix</span>
          </h3>
          <div className="space-y-3">
            {Object.entries(summary.dataSourceHealthMap || {}).map(([feed, status]) => (
              <div key={feed} className="flex justify-between items-center p-3 bg-slate-950/80 rounded-xl border border-slate-800 font-mono text-sm">
                <span className="text-slate-300">{feed}</span>
                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold ${
                    status === 'AVAILABLE'
                      ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                      : status === 'STALE'
                      ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                      : 'bg-rose-500/20 text-rose-400 border border-rose-500/30'
                  }`}
                >
                  {status}
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="p-6 bg-slate-900/70 rounded-2xl border border-slate-800 space-y-4">
          <h3 className="text-lg font-bold text-white flex items-center space-x-2">
            <ShieldCheck className="w-5 h-5 text-emerald-400" />
            <span>Core System Capabilities</span>
          </h3>
          <div className="space-y-3 font-sans text-sm text-slate-300">
            <div className="p-3 bg-slate-950/80 rounded-xl border border-slate-800 flex items-start space-x-3">
              <div className="w-2 h-2 rounded-full bg-cyan-400 mt-1.5 shrink-0" />
              <div>
                <p className="font-semibold text-white">Multi-Source Pre-Qualification</p>
                <p className="text-xs text-slate-400">Ingests OSHA safety records (EMR/TRIR), Certificate of Insurance (COI), financial credit profiles, and OFAC sanction watchlists.</p>
              </div>
            </div>
            <div className="p-3 bg-slate-950/80 rounded-xl border border-slate-800 flex items-start space-x-3">
              <div className="w-2 h-2 rounded-full bg-amber-400 mt-1.5 shrink-0" />
              <div>
                <p className="font-semibold text-white">Safety Gates & Dual-Control Approvals</p>
                <p className="text-xs text-slate-400">High-risk contractors (Score &gt; 65) or low data confidence (&lt; 0.75) trigger safety gates routing to the approval queue. Strictly forbids self-approval.</p>
              </div>
            </div>
            <div className="p-3 bg-slate-950/80 rounded-xl border border-slate-800 flex items-start space-x-3">
              <div className="w-2 h-2 rounded-full bg-indigo-400 mt-1.5 shrink-0" />
              <div>
                <p className="font-semibold text-white">Instant Status Rollback & Immutable Audit</p>
                <p className="text-xs text-slate-400">Restores prior vendor qualification state upon rollback while writing an append-only audit event preserving historical records.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
