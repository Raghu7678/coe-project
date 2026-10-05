import React, { useState, useEffect } from 'react';
import { History, Shield, Search, Download } from 'lucide-react';
import { AuditEvent } from '../types';
import { api } from '../services/api';

export const AuditTrailPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const loadLogs = async () => {
    setLoading(true);
    try {
      const res = await api.getAuditLogs();
      setLogs(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, []);

  const filteredLogs = logs.filter(
    (l) =>
      l.eventType.toLowerCase().includes(search.toLowerCase()) ||
      l.actor.toLowerCase().includes(search.toLowerCase()) ||
      l.targetEntity.toLowerCase().includes(search.toLowerCase()) ||
      l.details.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-wide flex items-center space-x-3">
            <History className="w-6 h-6 text-cyan-400" />
            <span>Immutable Audit Trail & Ledger</span>
          </h2>
          <p className="text-sm text-slate-400">
            Append-only historical ledger tracking every evaluation run, safety gate trigger, approval decision, remediation execution, stakeholder validation, and rollback.
          </p>
        </div>

        <a
          href={api.getAuditCsvExportUrl()}
          target="_blank"
          rel="noopener noreferrer"
          className="px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-cyan-400 text-xs font-mono font-bold rounded-xl border border-cyan-800/50 flex items-center space-x-2 transition-all"
        >
          <Download className="w-4 h-4" />
          <span>Export Audit Log CSV</span>
        </a>
      </div>

      <div className="flex justify-between items-center bg-slate-900/70 p-4 rounded-xl border border-slate-800">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
          <input
            type="text"
            placeholder="Search audit trail event, actor, entity..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-9 pr-4 py-2 text-sm text-white focus:outline-none focus:border-cyan-500 font-mono"
          />
        </div>

        <button onClick={loadLogs} className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-mono rounded-lg">
          Refresh Log Stream
        </button>
      </div>

      <div className="bg-slate-900/70 rounded-2xl border border-slate-800 overflow-hidden">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center">
          <h3 className="text-sm font-bold text-white uppercase font-mono tracking-wider">
            Audit Events Ledger ({filteredLogs.length})
          </h3>
          <span className="text-xs font-mono text-emerald-400 flex items-center space-x-1">
            <Shield className="w-3.5 h-3.5" />
            <span>CRYPTOGRAPHICALLY APPEND-ONLY</span>
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm font-sans">
            <thead className="bg-slate-950 text-xs font-mono text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3">Timestamp</th>
                <th className="px-4 py-3">Event Type</th>
                <th className="px-4 py-3">Actor / Principal</th>
                <th className="px-4 py-3">Target Entity</th>
                <th className="px-4 py-3">Audit Log Rationale & Evidence</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 font-mono text-slate-300 text-xs">
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-slate-500 text-sm">
                    Loading audit trail logs...
                  </td>
                </tr>
              ) : filteredLogs.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-slate-500 text-sm">
                    No matching audit events found.
                  </td>
                </tr>
              ) : (
                filteredLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 text-slate-400 whitespace-nowrap">
                      {new Date(log.timestamp).toLocaleString()}
                    </td>
                    <td className="px-4 py-3 font-bold">
                      <span
                        className={`px-2 py-0.5 rounded ${
                          log.eventType.includes('SAFETY') || log.eventType.includes('REJECTED')
                            ? 'bg-rose-500/20 text-rose-300 border border-rose-500/30'
                            : log.eventType.includes('ROLLBACK')
                            ? 'bg-purple-500/20 text-purple-300 border border-purple-500/30'
                            : log.eventType.includes('STAKEHOLDER')
                            ? 'bg-sky-500/20 text-sky-300 border border-sky-500/30'
                            : 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                        }`}
                      >
                        {log.eventType}
                      </span>
                    </td>
                    <td className="px-4 py-3 font-bold text-white">{log.actor}</td>
                    <td className="px-4 py-3 text-cyan-400">{log.targetEntity}</td>
                    <td className="px-4 py-3 text-slate-300 font-sans text-xs">{log.details}</td>
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
