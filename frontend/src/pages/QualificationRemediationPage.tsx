import React, { useState, useEffect } from 'react';
import { RefreshCw, Play, RotateCcw, CheckCircle2, FileText, Download, ShieldCheck, UserCheck } from 'lucide-react';
import { QualificationAction } from '../types';
import { api } from '../services/api';

export const QualificationRemediationPage: React.FC = () => {
  const [actions, setActions] = useState<QualificationAction[]>([]);
  const [loading, setLoading] = useState(true);
  const [rollbackActor, setRollbackActor] = useState('AUDITOR_OFFICER_01');
  const [rollbackReason, setRollbackReason] = useState('Manual override following updated evidence submission.');
  const [selectedAction, setSelectedAction] = useState<QualificationAction | null>(null);
  const [validatingAction, setValidatingAction] = useState<QualificationAction | null>(null);
  const [validatorName, setValidatorName] = useState('CHIEF_COMPLIANCE_OFFICER');
  const [validationNotes, setValidationNotes] = useState('Verified removal evidence against SIEM logs and cloud entitlement records.');
  const [msg, setMsg] = useState<string | null>(null);

  const loadActions = async () => {
    setLoading(true);
    try {
      const res = await api.getAllActions();
      setActions(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadActions();
  }, []);

  const handleExecute = async (id: number) => {
    setMsg(null);
    try {
      const res = await api.executeAction(id);
      setMsg(`Action #${id} (${res.actionType}) executed successfully.`);
      await loadActions();
    } catch (err) {
      alert((err as Error).message);
    }
  };

  const handleRollback = async (id: number) => {
    setMsg(null);
    try {
      const res = await api.rollbackAction(id, rollbackActor, rollbackReason);
      setMsg(`Action #${id} rolled back successfully! Restored state.`);
      setSelectedAction(null);
      await loadActions();
    } catch (err) {
      alert((err as Error).message);
    }
  };

  const handleValidate = async (id: number) => {
    setMsg(null);
    try {
      const res = await api.validateAction(id, validatorName, validationNotes);
      setMsg(`Action #${id} successfully validated by stakeholder ${res.validatedBy}!`);
      setValidatingAction(null);
      await loadActions();
    } catch (err) {
      alert((err as Error).message);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-wide flex items-center space-x-3">
            <RefreshCw className="w-6 h-6 text-cyan-400" />
            <span>Remediation Execution & Stakeholder Validation Manager</span>
          </h2>
          <p className="text-sm text-slate-400">
            Execute approved access removal actions, trigger state rollback, validate evidence trails, and export auditable compliance reports.
          </p>
        </div>

        {/* Export Action Buttons */}
        <div className="flex items-center space-x-3">
          <a
            href={api.getRemediationCsvExportUrl()}
            target="_blank"
            rel="noopener noreferrer"
            className="px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-cyan-400 text-xs font-mono font-bold rounded-xl border border-cyan-800/50 flex items-center space-x-2 transition-all"
          >
            <Download className="w-4 h-4" />
            <span>Export CSV Evidence</span>
          </a>

          <a
            href={api.getRemediationPdfExportUrl()}
            target="_blank"
            rel="noopener noreferrer"
            className="px-3.5 py-2 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white text-xs font-mono font-bold rounded-xl flex items-center space-x-2 shadow-lg shadow-cyan-950/40 transition-all"
          >
            <FileText className="w-4 h-4" />
            <span>Export PDF Report</span>
          </a>
        </div>
      </div>

      {msg && (
        <div className="p-4 bg-cyan-950/80 border border-cyan-500/40 rounded-xl text-cyan-300 text-sm font-mono flex items-center space-x-3">
          <CheckCircle2 className="w-5 h-5 text-cyan-400 shrink-0" />
          <span>{msg}</span>
        </div>
      )}

      <div className="bg-slate-900/70 rounded-2xl border border-slate-800 overflow-hidden shadow-xl">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center">
          <h3 className="text-sm font-bold text-white uppercase font-mono tracking-wider">
            Remediation Actions & Evidence Trail ({actions.length})
          </h3>
          <button onClick={loadActions} className="px-3 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-mono rounded-lg">
            Refresh List
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm font-sans">
            <thead className="bg-slate-950 text-xs font-mono text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3">Identity / Target</th>
                <th className="px-4 py-3">Action Type</th>
                <th className="px-4 py-3">Permission Shift</th>
                <th className="px-4 py-3">Action Status</th>
                <th className="px-4 py-3">Stakeholder Validation</th>
                <th className="px-4 py-3 text-right">Governance Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 font-sans text-slate-300">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-slate-500 font-mono text-sm">
                    Loading remediation actions...
                  </td>
                </tr>
              ) : actions.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-slate-500 font-mono text-sm">
                    No remediation actions recorded.
                  </td>
                </tr>
              ) : (
                actions.map((act) => (
                  <tr key={act.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3">
                      <div className="font-bold text-white">{act.fullName || act.companyName || 'Identity'}</div>
                      <div className="text-xs font-mono text-cyan-400">{act.username || act.vendorCode}</div>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-200">
                      <div>{act.actionType}</div>
                      <div className="text-[11px] text-slate-400">{act.appName}</div>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs">
                      <span className="text-slate-400">{act.previousPermissionLevel || act.previousQualificationStatus || 'ADMIN'}</span>
                      <span className="mx-2 text-cyan-400">→</span>
                      <span className="font-bold text-white">{act.targetPermissionLevel || act.targetQualificationStatus || 'NONE'}</span>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs font-bold">
                      <span
                        className={`px-2.5 py-1 rounded-md ${
                          act.status === 'EXECUTED'
                            ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40'
                            : act.status === 'APPROVED'
                            ? 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/40'
                            : act.status === 'ROLLED_BACK'
                            ? 'bg-purple-500/20 text-purple-400 border border-purple-500/40'
                            : 'bg-amber-500/20 text-amber-400 border border-amber-500/40'
                        }`}
                      >
                        {act.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs">
                      {act.stakeholderValidated ? (
                        <div className="flex items-center space-x-1.5 text-sky-400 bg-sky-950/60 border border-sky-800/60 px-2 py-1 rounded">
                          <ShieldCheck className="w-4 h-4 text-sky-400" />
                          <span>Validated ({act.validatedBy})</span>
                        </div>
                      ) : (
                        <span className="text-slate-500 italic">Pending Sign-off</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-right space-x-2">
                      {act.status === 'APPROVED' && (
                        <button
                          onClick={() => handleExecute(act.id)}
                          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-lg transition-all inline-flex items-center space-x-1"
                        >
                          <Play className="w-3.5 h-3.5 fill-white" />
                          <span>Execute</span>
                        </button>
                      )}

                      {act.status === 'EXECUTED' && (
                        <>
                          {!act.stakeholderValidated && (
                            <button
                              onClick={() => setValidatingAction(act)}
                              className="px-3 py-1.5 bg-sky-600 hover:bg-sky-500 text-white text-xs font-semibold rounded-lg transition-all inline-flex items-center space-x-1"
                            >
                              <UserCheck className="w-3.5 h-3.5" />
                              <span>Validate</span>
                            </button>
                          )}

                          <button
                            onClick={() => setSelectedAction(act)}
                            className="px-3 py-1.5 bg-amber-600 hover:bg-amber-500 text-white text-xs font-semibold rounded-lg transition-all inline-flex items-center space-x-1"
                          >
                            <RotateCcw className="w-3.5 h-3.5" />
                            <span>Rollback</span>
                          </button>
                        </>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Stakeholder Validation Sign-off Modal */}
      {validatingAction && (
        <div className="p-6 bg-slate-900/95 rounded-2xl border border-sky-500/40 space-y-4 shadow-2xl">
          <h3 className="text-lg font-bold text-sky-300 flex items-center space-x-2 font-mono">
            <ShieldCheck className="w-5 h-5 text-sky-400" />
            <span>Stakeholder Evidence Validation & Compliance Sign-Off</span>
          </h3>

          <p className="text-xs text-slate-300 font-mono">
            Sign off on remediation evidence for <strong>{validatingAction.fullName || validatingAction.username}</strong> ({validatingAction.actionType} on {validatingAction.appName}).
          </p>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-mono text-slate-400 mb-1">Stakeholder / Auditor Name:</label>
              <input
                type="text"
                value={validatorName}
                onChange={(e) => setValidatorName(e.target.value)}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white font-mono"
              />
            </div>

            <div>
              <label className="block text-xs font-mono text-slate-400 mb-1">Validation Audit Notes:</label>
              <input
                type="text"
                value={validationNotes}
                onChange={(e) => setValidationNotes(e.target.value)}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white font-sans"
              />
            </div>
          </div>

          <div className="flex space-x-3 pt-2">
            <button
              onClick={() => handleValidate(validatingAction.id)}
              className="px-4 py-2 bg-sky-600 hover:bg-sky-500 text-white font-bold text-sm rounded-xl font-mono flex items-center space-x-2"
            >
              <ShieldCheck className="w-4 h-4" />
              <span>STAMP STAKEHOLDER VALIDATION</span>
            </button>
            <button
              onClick={() => setValidatingAction(null)}
              className="px-4 py-2 bg-slate-800 text-slate-300 hover:text-white font-mono text-sm rounded-xl"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      {/* Rollback Confirmation Box */}
      {selectedAction && (
        <div className="p-6 bg-slate-900/90 rounded-2xl border border-amber-500/40 space-y-4">
          <h3 className="text-lg font-bold text-amber-300 flex items-center space-x-2 font-mono">
            <RotateCcw className="w-5 h-5 text-amber-400" />
            <span>Confirm Instant Access Status Rollback</span>
          </h3>

          <p className="text-xs text-slate-300 font-mono">
            Rolling back Action #{selectedAction.id} for <strong>{selectedAction.fullName || selectedAction.username}</strong> will restore permission level to{' '}
            <strong className="text-emerald-400">{selectedAction.previousPermissionLevel || 'ADMIN'}</strong>.
          </p>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-mono text-slate-400 mb-1">Auditor / Officer Name:</label>
              <input
                type="text"
                value={rollbackActor}
                onChange={(e) => setRollbackActor(e.target.value)}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white font-mono"
              />
            </div>

            <div>
              <label className="block text-xs font-mono text-slate-400 mb-1">Rollback Rationale:</label>
              <input
                type="text"
                value={rollbackReason}
                onChange={(e) => setRollbackReason(e.target.value)}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white font-sans"
              />
            </div>
          </div>

          <div className="flex space-x-3 pt-2">
            <button
              onClick={() => handleRollback(selectedAction.id)}
              className="px-4 py-2 bg-amber-600 hover:bg-amber-500 text-white font-bold text-sm rounded-xl font-mono"
            >
              CONFIRM ROLLBACK & RESTORE STATE
            </button>
            <button
              onClick={() => setSelectedAction(null)}
              className="px-4 py-2 bg-slate-800 text-slate-300 hover:text-white font-mono text-sm rounded-xl"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
