import React, { useState, useEffect } from 'react';
import { ArrowLeft, ShieldAlert, FileText, Activity, Clock, CheckCircle } from 'lucide-react';
import { ContractorRiskAnomaly } from '../types';
import { api } from '../services/api';

interface AnomalyDetailPageProps {
  anomalyId: number;
  onBack: () => void;
}

export const AnomalyDetailPage: React.FC<AnomalyDetailPageProps> = ({ anomalyId, onBack }) => {
  const [anomaly, setAnomaly] = useState<ContractorRiskAnomaly | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api
      .getAnomalyById(anomalyId)
      .then((res) => setAnomaly(res))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [anomalyId]);

  if (loading) {
    return <div className="p-8 text-center text-slate-400 font-mono">Loading anomaly breakdown details...</div>;
  }

  if (!anomaly) {
    return (
      <div className="p-8 text-center space-y-4">
        <p className="text-slate-400">Anomaly record not found.</p>
        <button onClick={onBack} className="px-4 py-2 bg-slate-800 text-white text-xs rounded-xl font-mono">
          Back to Hub
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <button
        onClick={onBack}
        className="px-3 py-1.5 bg-slate-900 border border-slate-800 text-slate-300 hover:text-white rounded-lg text-xs font-mono flex items-center space-x-2"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Risk Evaluation Hub</span>
      </button>

      <div className="p-6 bg-slate-900/80 rounded-2xl border border-slate-800 space-y-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-6 border-b border-slate-800">
          <div>
            <div className="flex items-center space-x-3">
              <h2 className="text-2xl font-bold text-white">{anomaly.companyName}</h2>
              <span className="px-2.5 py-1 text-xs font-mono font-bold bg-cyan-950 text-cyan-400 border border-cyan-500/40 rounded-lg">
                {anomaly.vendorCode}
              </span>
            </div>
            <p className="text-sm text-slate-400 mt-1 font-mono">{anomaly.anomalyType}</p>
          </div>

          <div className="flex items-center space-x-3 font-mono">
            <div className="text-right">
              <p className="text-xs text-slate-400">Risk Score</p>
              <p className="text-xl font-extrabold text-white">{anomaly.compositeRiskScore.toFixed(1)} / 100</p>
            </div>
            <span
              className={`px-3 py-1.5 rounded-xl text-sm font-bold ${
                anomaly.riskLevel === 'CRITICAL'
                  ? 'bg-rose-500/20 text-rose-400 border border-rose-500/40'
                  : 'bg-amber-500/20 text-amber-400 border border-amber-500/40'
              }`}
            >
              {anomaly.riskLevel} RISK
            </span>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 space-y-1 font-mono">
            <p className="text-xs text-slate-400 flex items-center space-x-1">
              <Activity className="w-3.5 h-3.5 text-cyan-400" />
              <span>Data Confidence</span>
            </p>
            <p className="text-lg font-bold text-white">{(anomaly.dataConfidenceScore * 100).toFixed(0)}%</p>
            <p className="text-[11px] text-slate-500">Multi-source freshness</p>
          </div>

          <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 space-y-1 font-mono">
            <p className="text-xs text-slate-400 flex items-center space-x-1">
              <Clock className="w-3.5 h-3.5 text-amber-400" />
              <span>Target Remediation SLA</span>
            </p>
            <p className="text-lg font-bold text-white">{anomaly.slaTargetMinutes} Minutes</p>
            <p className="text-[11px] text-slate-500">Strict compliance SLA</p>
          </div>

          <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 space-y-1 font-mono">
            <p className="text-xs text-slate-400 flex items-center space-x-1">
              <ShieldAlert className="w-3.5 h-3.5 text-rose-400" />
              <span>Safety Gate Status</span>
            </p>
            <p className="text-lg font-bold text-rose-400">
              {anomaly.safetyGateTriggered ? 'ENFORCED' : 'AUTO-PASSED'}
            </p>
            <p className="text-[11px] text-slate-500">
              {anomaly.safetyGateTriggered ? 'Routed to approval queue' : 'Automatic clearance'}
            </p>
          </div>
        </div>

        <div className="space-y-4">
          <h3 className="text-sm font-bold text-white uppercase font-mono tracking-wider flex items-center space-x-2">
            <FileText className="w-4 h-4 text-cyan-400" />
            <span>Risk Finding Description</span>
          </h3>
          <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 text-sm text-slate-300 font-sans">
            {anomaly.description}
          </div>
        </div>

        <div className="space-y-4">
          <h3 className="text-sm font-bold text-white uppercase font-mono tracking-wider flex items-center space-x-2">
            <CheckCircle className="w-4 h-4 text-emerald-400" />
            <span>Multi-Source Ingested Evidence Details</span>
          </h3>
          <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 font-mono text-xs text-cyan-300 whitespace-pre-wrap">
            {anomaly.evidenceDetails}
          </div>
        </div>
      </div>
    </div>
  );
};
