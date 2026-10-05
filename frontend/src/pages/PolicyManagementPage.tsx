import React, { useState, useEffect } from 'react';
import { Sliders, Save, CheckCircle2 } from 'lucide-react';
import { RiskThresholdPolicy } from '../types';
import { api } from '../services/api';

export const PolicyManagementPage: React.FC = () => {
  const [policy, setPolicy] = useState<RiskThresholdPolicy | null>(null);
  const [loading, setLoading] = useState(true);
  const [savedMsg, setSavedMsg] = useState(false);

  useEffect(() => {
    api
      .getPolicy()
      .then((res) => setPolicy(res))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async () => {
    if (!policy) return;
    try {
      const updated = await api.updatePolicy(policy);
      setPolicy(updated);
      setSavedMsg(true);
      setTimeout(() => setSavedMsg(false), 3000);
    } catch (err) {
      alert((err as Error).message);
    }
  };

  if (loading || !policy) {
    return <div className="p-8 text-center text-slate-400 font-mono">Loading risk threshold policy configuration...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-wide flex items-center space-x-3">
            <Sliders className="w-6 h-6 text-cyan-400" />
            <span>Risk Threshold & Policy Configuration</span>
          </h2>
          <p className="text-sm text-slate-400">Configure safety thresholds (EMR/TRIR), minimum insurance limits, credit score requirements, and weighted risk formulas.</p>
        </div>

        <button
          onClick={handleSave}
          className="px-4 py-2 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white text-sm font-semibold rounded-xl flex items-center space-x-2 shadow-lg shadow-cyan-950/40"
        >
          <Save className="w-4 h-4" />
          <span>Save Policy</span>
        </button>
      </div>

      {savedMsg && (
        <div className="p-4 bg-emerald-950/80 border border-emerald-500/40 rounded-xl text-emerald-300 text-sm font-mono flex items-center space-x-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          <span>Policy configuration saved successfully.</span>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 bg-slate-900/70 rounded-2xl border border-slate-800 space-y-4 font-mono">
          <h3 className="text-sm font-bold text-white uppercase text-cyan-400">Safety & Insurance Thresholds</h3>

          <div className="space-y-3 text-xs">
            <div>
              <label className="block text-slate-400 mb-1">Max Allowed EMR Rate (Experience Mod Rate):</label>
              <input
                type="number"
                step="0.05"
                value={policy.maxAllowedEmr}
                onChange={(e) => setPolicy({ ...policy, maxAllowedEmr: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>

            <div>
              <label className="block text-slate-400 mb-1">Max Allowed TRIR Rate (Recordable Incident Rate):</label>
              <input
                type="number"
                step="0.1"
                value={policy.maxAllowedTrir}
                onChange={(e) => setPolicy({ ...policy, maxAllowedTrir: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>

            <div>
              <label className="block text-slate-400 mb-1">Min Insurance Coverage Limit ($ Millions):</label>
              <input
                type="number"
                step="0.5"
                value={policy.minInsuranceLimitMillion}
                onChange={(e) => setPolicy({ ...policy, minInsuranceLimitMillion: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>

            <div>
              <label className="block text-slate-400 mb-1">Min Required Financial Credit Score (0-100):</label>
              <input
                type="number"
                value={policy.minCreditScore}
                onChange={(e) => setPolicy({ ...policy, minCreditScore: parseInt(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>
          </div>
        </div>

        <div className="p-6 bg-slate-900/70 rounded-2xl border border-slate-800 space-y-4 font-mono">
          <h3 className="text-sm font-bold text-white uppercase text-cyan-400">Risk Weightings & Safety Gate Threshold</h3>

          <div className="space-y-3 text-xs">
            <div>
              <label className="block text-slate-400 mb-1">Safety Risk Weight (0.0 - 1.0):</label>
              <input
                type="number"
                step="0.05"
                value={policy.safetyWeight}
                onChange={(e) => setPolicy({ ...policy, safetyWeight: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>

            <div>
              <label className="block text-slate-400 mb-1">Financial Stability Risk Weight (0.0 - 1.0):</label>
              <input
                type="number"
                step="0.05"
                value={policy.financialWeight}
                onChange={(e) => setPolicy({ ...policy, financialWeight: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>

            <div>
              <label className="block text-slate-400 mb-1">Insurance Compliance Risk Weight (0.0 - 1.0):</label>
              <input
                type="number"
                step="0.05"
                value={policy.insuranceWeight}
                onChange={(e) => setPolicy({ ...policy, insuranceWeight: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>

            <div>
              <label className="block text-slate-400 mb-1">Confidence Safety Gate Threshold (Default 0.75):</label>
              <input
                type="number"
                step="0.05"
                value={policy.confidenceSafetyGateThreshold}
                onChange={(e) => setPolicy({ ...policy, confidenceSafetyGateThreshold: parseFloat(e.target.value) })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-white"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
