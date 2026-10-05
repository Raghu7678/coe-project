import React, { useState, useEffect } from 'react';
import { Activity, Server, RefreshCw, AlertTriangle } from 'lucide-react';
import { DataSourceHealth, HealthState } from '../types';
import { api } from '../services/api';

export const DataSourceHealthPage: React.FC = () => {
  const [healthList, setHealthList] = useState<DataSourceHealth[]>([]);
  const [loading, setLoading] = useState(true);

  const loadHealth = async () => {
    setLoading(true);
    try {
      const res = await api.getDataSourceHealth();
      setHealthList(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHealth();
  }, []);

  const handleUpdateStatus = async (sourceName: string, status: HealthState) => {
    try {
      const staleness = status === 'STALE' ? 24.0 : status === 'DELAYED' ? 72.0 : status === 'UNAVAILABLE' ? 168.0 : 0.5;
      const latency = status === 'UNAVAILABLE' ? 9999 : status === 'DELAYED' ? 4500 : status === 'STALE' ? 850 : 25;
      await api.updateDataSourceHealth(sourceName, status, latency, staleness);
      await loadHealth();
    } catch (err) {
      alert((err as Error).message);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-white tracking-wide flex items-center space-x-3">
          <Activity className="w-6 h-6 text-cyan-400" />
          <span>Multi-Source Feeds Health Console</span>
        </h2>
        <p className="text-sm text-slate-400">
          Monitor and simulate data source health states (Financial, OSHA Safety, Insurance COI, Sanctions Watchlist) to observe dynamic confidence calculation.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {loading ? (
          <div className="col-span-2 text-center py-12 text-slate-500 font-mono">Loading data feeds...</div>
        ) : (
          healthList.map((feed) => (
            <div key={feed.id} className="p-6 bg-slate-900/70 rounded-2xl border border-slate-800 space-y-4">
              <div className="flex justify-between items-start">
                <div className="flex items-center space-x-3">
                  <div className="p-3 bg-slate-950 rounded-xl border border-slate-800">
                    <Server className="w-5 h-5 text-cyan-400" />
                  </div>
                  <div>
                    <h3 className="text-base font-bold text-white font-mono">{feed.sourceName}</h3>
                    <p className="text-xs text-slate-400 font-mono">
                      Staleness: {feed.dataStalenessHours}h | Latency: {feed.latencyMs}ms
                    </p>
                  </div>
                </div>

                <span
                  className={`px-3 py-1 rounded-full text-xs font-mono font-bold ${
                    feed.status === 'AVAILABLE'
                      ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                      : feed.status === 'STALE'
                      ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                      : 'bg-rose-500/20 text-rose-400 border border-rose-500/30'
                  }`}
                >
                  {feed.status}
                </span>
              </div>

              <div className="pt-3 border-t border-slate-800 space-y-2">
                <p className="text-xs text-slate-400 font-mono">Simulate Feed Status Override:</p>
                <div className="grid grid-cols-4 gap-2 text-xs font-mono">
                  {(['AVAILABLE', 'STALE', 'DELAYED', 'UNAVAILABLE'] as HealthState[]).map((st) => (
                    <button
                      key={st}
                      onClick={() => handleUpdateStatus(feed.sourceName, st)}
                      className={`py-1.5 rounded-lg border transition-all ${
                        feed.status === st
                          ? 'bg-cyan-600 text-white font-bold border-cyan-400 shadow-md shadow-cyan-950'
                          : 'bg-slate-950 text-slate-400 border-slate-800 hover:text-white'
                      }`}
                    >
                      {st}
                    </button>
                  ))}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
