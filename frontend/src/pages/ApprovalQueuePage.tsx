import React, { useState, useEffect } from 'react';
import { CheckSquare, CheckCircle, XCircle, AlertCircle, UserCheck } from 'lucide-react';
import { QualificationAction } from '../types';
import { api } from '../services/api';

export const ApprovalQueuePage: React.FC = () => {
  const [pending, setPending] = useState<QualificationAction[]>([]);
  const [loading, setLoading] = useState(true);
  const [reviewerId, setReviewerId] = useState('PROCUREMENT_OFFICER_01');
  const [comments, setComments] = useState('');
  const [selectedActionId, setSelectedActionId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadPending = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getPendingApprovals();
      setPending(res);
      if (res.length > 0 && !selectedActionId) {
        setSelectedActionId(res[0].id);
      }
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPending();
  }, []);

  const handleDecision = async (decision: 'APPROVED' | 'REJECTED') => {
    if (!selectedActionId) return;
    setError(null);
    setSuccess(null);
    try {
      const updated = await api.processApprovalDecision(selectedActionId, reviewerId, decision, comments);
      setSuccess(`Decision successfully recorded: ${updated.actionType} is now ${updated.status}.`);
      setComments('');
      await loadPending();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const selectedAction = pending.find((a) => a.id === selectedActionId);

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-white tracking-wide flex items-center space-x-3">
          <CheckSquare className="w-6 h-6 text-cyan-400" />
          <span>Accountable Approval Queue (Dual-Control)</span>
        </h2>
        <p className="text-sm text-slate-400">
          High-risk flags and low-confidence evaluations routed by safety gates. Enforces strict dual-control (no self-approval).
        </p>
      </div>

      {error && (
        <div className="p-4 bg-rose-950/80 border border-rose-500/40 rounded-xl text-rose-300 text-sm flex items-start space-x-3 font-mono">
          <AlertCircle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
          <div>
            <p className="font-bold text-rose-200">Approval Policy Error</p>
            <p>{error}</p>
          </div>
        </div>
      )}

      {success && (
        <div className="p-4 bg-emerald-950/80 border border-emerald-500/40 rounded-xl text-emerald-300 text-sm flex items-center space-x-3 font-mono">
          <CheckCircle className="w-5 h-5 text-emerald-400 shrink-0" />
          <span>{success}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1 bg-slate-900/70 rounded-2xl border border-slate-800 p-4 space-y-3">
          <h3 className="text-sm font-bold text-white uppercase font-mono tracking-wider">
            Pending Reviews ({pending.length})
          </h3>
          {loading ? (
            <div className="py-8 text-center text-xs font-mono text-slate-500">Loading queue...</div>
          ) : pending.length === 0 ? (
            <div className="py-8 text-center text-xs font-mono text-slate-500">No pending approvals in queue.</div>
          ) : (
            <div className="space-y-2">
              {pending.map((item) => (
                <button
                  key={item.id}
                  onClick={() => setSelectedActionId(item.id)}
                  className={`w-full text-left p-3.5 rounded-xl border transition-all ${
                    selectedActionId === item.id
                      ? 'bg-slate-800 border-cyan-500/50 shadow-md shadow-cyan-950/50'
                      : 'bg-slate-950/60 border-slate-800 hover:bg-slate-800/50'
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <span className="font-bold text-white text-sm">{item.companyName}</span>
                    <span className="text-[10px] font-mono text-cyan-400">{item.vendorCode}</span>
                  </div>
                  <p className="text-xs font-mono text-slate-400 mt-1">{item.actionType}</p>
                  <div className="flex justify-between items-center mt-2 text-[10px] font-mono text-slate-500">
                    <span>Initiated by: {item.initiatedBy}</span>
                    <span className="text-amber-400 font-semibold">{item.targetQualificationStatus}</span>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="lg:col-span-2 bg-slate-900/70 rounded-2xl border border-slate-800 p-6 space-y-6">
          {selectedAction ? (
            <>
              <div className="pb-4 border-b border-slate-800 space-y-2">
                <div className="flex justify-between items-center">
                  <h3 className="text-xl font-bold text-white">{selectedAction.companyName}</h3>
                  <span className="px-3 py-1 bg-amber-500/20 text-amber-400 border border-amber-500/40 rounded-full font-mono text-xs font-bold">
                    PENDING DUAL CONTROL
                  </span>
                </div>
                <p className="text-xs font-mono text-cyan-400">
                  Vendor Code: {selectedAction.vendorCode} | Proposed Action: {selectedAction.actionType}
                </p>
              </div>

              <div className="space-y-2 font-mono text-xs">
                <p className="text-slate-400 font-bold uppercase">Rationale & Evidence:</p>
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-slate-300 font-sans text-sm">
                  {selectedAction.rationale}
                </div>
              </div>

              <div className="p-4 bg-slate-950/80 rounded-xl border border-slate-800 space-y-4">
                <h4 className="text-sm font-bold text-white font-mono flex items-center space-x-2">
                  <UserCheck className="w-4 h-4 text-cyan-400" />
                  <span>Reviewer Identity & Accountable Decision</span>
                </h4>

                <div className="space-y-3">
                  <div>
                    <label className="block text-xs font-mono text-slate-400 mb-1">
                      Reviewer ID / Officer Name (Must be distinct from initiator <strong className="text-white">{selectedAction.initiatedBy}</strong>):
                    </label>
                    <input
                      type="text"
                      value={reviewerId}
                      onChange={(e) => setReviewerId(e.target.value)}
                      placeholder="e.g. PROCUREMENT_OFFICER_01"
                      className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white font-mono focus:outline-none focus:border-cyan-500"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-slate-400 mb-1">Review Comments / Governance Rationale:</label>
                    <textarea
                      rows={3}
                      value={comments}
                      onChange={(e) => setComments(e.target.value)}
                      placeholder="Specify decision rationale for audit log..."
                      className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white font-sans focus:outline-none focus:border-cyan-500"
                    />
                  </div>
                </div>

                <div className="flex space-x-3 pt-2">
                  <button
                    onClick={() => handleDecision('APPROVED')}
                    className="flex-1 py-2.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-bold text-sm rounded-xl flex items-center justify-center space-x-2 shadow-lg shadow-emerald-950/40"
                  >
                    <CheckCircle className="w-4 h-4" />
                    <span>APPROVE QUALIFICATION ACTION</span>
                  </button>

                  <button
                    onClick={() => handleDecision('REJECTED')}
                    className="flex-1 py-2.5 bg-gradient-to-r from-rose-600 to-pink-600 hover:from-rose-500 hover:to-pink-500 text-white font-bold text-sm rounded-xl flex items-center justify-center space-x-2 shadow-lg shadow-rose-950/40"
                  >
                    <XCircle className="w-4 h-4" />
                    <span>REJECT / DISQUALIFY</span>
                  </button>
                </div>
              </div>
            </>
          ) : (
            <div className="py-12 text-center text-slate-500 font-mono text-sm">
              Select an item from the queue to process approval.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
