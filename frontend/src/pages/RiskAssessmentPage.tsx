import React, { useState, useEffect } from 'react';
import { Play, Filter, AlertTriangle, ShieldCheck, Search, Eye } from 'lucide-react';
import { ContractorRiskAnomaly, Contractor } from '../types';
import { api } from '../services/api';

interface RiskAssessmentPageProps {
  onSelectAnomaly: (id: number) => void;
}

export const RiskAssessmentPage: React.FC<RiskAssessmentPageProps> = ({ onSelectAnomaly }) => {
  const [anomalies, setAnomalies] = useState<ContractorRiskAnomaly[]>([]);
  const [contractors, setContractors] = useState<Contractor[]>([]);
  const [loading, setLoading] = useState(false);
  const [engine, setEngine] = useState<'PROTOTYPE' | 'BASELINE'>('PROTOTYPE');
  const [search, setSearch] = useState('');
  const [riskFilter, setRiskFilter] = useState<string>('ALL');

  const loadData = async () => {
    setLoading(true);
    try {
      const [anomRes, contRes] = await Promise.all([
        api.getAnomalies(engine),
        api.getContractors()
      ]);
      setAnomalies(anomRes);
      setContractors(contRes);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [engine]);

  const handleRunAssessment = async () => {
    setLoading(true);
    try {
      const res = await api.runRiskAssessment(engine);
      setAnomalies(res);
      const updatedCont = await api.getContractors();
      setContractors(updatedCont);
    } catch (err) {
      alert('Error running risk assessment: ' + (err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  const filteredAnomalies = anomalies.filter((a) => {
    const matchesSearch =
      a.vendorCode.toLowerCase().includes(search.toLowerCase()) ||
      a.companyName.toLowerCase().includes(search.toLowerCase()) ||
      a.anomalyType.toLowerCase().includes(search.toLowerCase());
    const matchesRisk = riskFilter === 'ALL' || a.riskLevel === riskFilter;
    return matchesSearch && matchesRisk;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-wide">Contractor Risk Evaluation Hub</h2>
          <p className="text-sm text-slate-400">Trigger multi-source risk assessment across safety, insurance compliance, financial stability, and sanctions.</p>
        </div>

        <div className="flex items-center space-x-3">
          <div className="bg-slate-900 p-1 rounded-xl border border-slate-800 flex text-xs font-mono">
            <button
              onClick={() => setEngine('PROTOTYPE')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                engine === 'PROTOTYPE'
                  ? 'bg-cyan-600 text-white font-bold shadow-md shadow-cyan-900/50'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Prototype Engine (Multi-Source)
            </button>
            <button
              onClick={() => setEngine('BASELINE')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                engine === 'BASELINE'
                  ? 'bg-slate-700 text-white font-bold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Naive Baseline (Single-Source)
            </button>
          </div>

          <button
            onClick={handleRunAssessment}
            disabled={loading}
            className="px-4 py-2 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white text-sm font-semibold rounded-xl flex items-center space-x-2 shadow-lg shadow-cyan-900/30 transition-all disabled:opacity-50"
          >
            <Play className="w-4 h-4 fill-white" />
            <span>{loading ? 'Evaluating...' : 'Run Assessment'}</span>
          </button>
        </div>
      </div>

      <div className="flex flex-col md:flex-row gap-4 justify-between items-center bg-slate-900/70 p-4 rounded-xl border border-slate-800">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
          <input
            type="text"
            placeholder="Search vendor code, company name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-9 pr-4 py-2 text-sm text-white focus:outline-none focus:border-cyan-500 font-mono"
          />
        </div>

        <div className="flex items-center space-x-3 w-full md:w-auto">
          <Filter className="w-4 h-4 text-slate-400" />
          <span className="text-xs text-slate-400 font-mono">Risk Level:</span>
          <select
            value={riskFilter}
            onChange={(e) => setRiskFilter(e.target.value)}
            className="bg-slate-950 border border-slate-800 text-slate-200 text-sm rounded-lg px-3 py-1.5 focus:outline-none focus:border-cyan-500 font-mono"
          >
            <option value="ALL">All Risk Levels</option>
            <option value="CRITICAL">CRITICAL</option>
            <option value="HIGH">HIGH</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="LOW">LOW</option>
          </select>
        </div>
      </div>

      <div className="bg-slate-900/70 rounded-2xl border border-slate-800 overflow-hidden">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center">
          <h3 className="text-sm font-bold text-white font-mono uppercase tracking-wider">
            Detected Risk Anomalies ({filteredAnomalies.length})
          </h3>
          <span className="text-xs font-mono text-slate-400">
            Engine Mode: <strong className="text-cyan-400">{engine}</strong>
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm font-sans">
            <thead className="bg-slate-950 text-xs font-mono text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3">Vendor</th>
                <th className="px-4 py-3">Anomaly Type</th>
                <th className="px-4 py-3">Risk Level</th>
                <th className="px-4 py-3">Risk Score</th>
                <th className="px-4 py-3">Confidence</th>
                <th className="px-4 py-3">Safety Gate</th>
                <th className="px-4 py-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 font-sans text-slate-300">
              {filteredAnomalies.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-slate-500 font-mono text-sm">
                    No risk anomalies found for the selected filter.
                  </td>
                </tr>
              ) : (
                filteredAnomalies.map((anom) => (
                  <tr key={anom.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3">
                      <div className="font-bold text-white">{anom.companyName}</div>
                      <div className="text-xs font-mono text-cyan-400">{anom.vendorCode}</div>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-200">
                      {anom.anomalyType}
                    </td>
                    <td className="px-4 py-3 font-mono text-xs font-bold">
                      <span
                        className={`px-2.5 py-1 rounded-md ${
                          anom.riskLevel === 'CRITICAL'
                            ? 'bg-rose-500/20 text-rose-400 border border-rose-500/40'
                            : anom.riskLevel === 'HIGH'
                            ? 'bg-amber-500/20 text-amber-400 border border-amber-500/40'
                            : 'bg-blue-500/20 text-blue-400 border border-blue-500/40'
                        }`}
                      >
                        {anom.riskLevel}
                      </span>
                    </td>
                    <td className="px-4 py-3 font-mono font-bold text-white">
                      {anom.compositeRiskScore.toFixed(1)}
                    </td>
                    <td className="px-4 py-3 font-mono text-xs">
                      <span
                        className={
                          anom.dataConfidenceScore >= 0.75 ? 'text-emerald-400 font-bold' : 'text-amber-400 font-bold'
                        }
                      >
                        {(anom.dataConfidenceScore * 100).toFixed(0)}%
                      </span>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs">
                      {anom.safetyGateTriggered ? (
                        <span className="px-2 py-0.5 rounded bg-rose-500/20 text-rose-300 border border-rose-500/30 font-semibold">
                          ENFORCED
                        </span>
                      ) : (
                        <span className="text-slate-500">AUTO-CLEARED</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-right">
                      <button
                        onClick={() => onSelectAnomaly(anom.id)}
                        className="px-3 py-1.5 bg-slate-800 hover:bg-cyan-950 hover:text-cyan-300 border border-slate-700 text-xs font-semibold rounded-lg transition-all inline-flex items-center space-x-1"
                      >
                        <Eye className="w-3.5 h-3.5" />
                        <span>Inspect</span>
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
