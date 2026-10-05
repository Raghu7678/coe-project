import React, { useState, useEffect } from 'react';
import { BarChart3, Play, CheckCircle2, TrendingUp, AlertCircle, Clock, ShieldCheck, Download, Layers } from 'lucide-react';
import { EvaluationMetrics, ExperimentResult } from '../types';
import { api } from '../services/api';

export const EvaluationMetricsPage: React.FC = () => {
  const [metrics, setMetrics] = useState<EvaluationMetrics[]>([]);
  const [experiment, setExperiment] = useState<ExperimentResult | null>(null);
  const [trials, setTrials] = useState<number>(10);
  const [loading, setLoading] = useState(false);

  const runFullExperiment = async () => {
    setLoading(true);
    try {
      const [compRes, expRes] = await Promise.all([
        api.runEvaluationExperiment(),
        api.getMultiTrialExperiment(trials)
      ]);
      setMetrics(compRes);
      setExperiment(expRes);
    } catch (err) {
      console.error('Experiment load error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    runFullExperiment();
  }, [trials]);

  const baseline = metrics.find((m) => m.engineName.toLowerCase().includes('baseline'));
  const prototype = metrics.find((m) => m.engineName.toLowerCase().includes('prototype'));

  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-4 border-b border-slate-800">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-wide flex items-center space-x-3">
            <BarChart3 className="w-7 h-7 text-cyan-400" />
            <span>Defensible Empirical Evaluation & Benchmark Experiment</span>
          </h2>
          <p className="text-sm text-slate-400 mt-1">
            Statistical multi-trial experiment (N={trials} runs), variance analysis, 95% CIs, SLA MTTR tracking (15/60/1440m), and grounded error analysis.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <div className="flex items-center space-x-2 bg-slate-900 px-3 py-1.5 rounded-xl border border-slate-800 text-xs font-mono">
            <span className="text-slate-400">Trials:</span>
            <select
              value={trials}
              onChange={(e) => setTrials(Number(e.target.value))}
              className="bg-slate-950 text-cyan-400 font-bold border border-slate-700 rounded px-2 py-0.5 focus:outline-none"
            >
              <option value={5}>5 Trials</option>
              <option value={10}>10 Trials</option>
              <option value={25}>25 Trials</option>
            </select>
          </div>

          <button
            onClick={runFullExperiment}
            disabled={loading}
            className="px-4 py-2 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white text-sm font-semibold rounded-xl flex items-center space-x-2 shadow-lg shadow-cyan-950/40 transition-all"
          >
            <Play className="w-4 h-4 fill-white" />
            <span>{loading ? 'Simulating Runs...' : 'Re-Run Multi-Trial Experiment'}</span>
          </button>
        </div>
      </div>

      {/* Statistical Variance Summary Grid */}
      {experiment && (
        <div className="p-6 bg-gradient-to-br from-slate-900 via-slate-900/90 to-cyan-950/30 rounded-2xl border border-cyan-800/50 space-y-6 shadow-2xl">
          <div className="flex justify-between items-center pb-3 border-b border-slate-800">
            <div className="flex items-center space-x-3">
              <TrendingUp className="w-5 h-5 text-emerald-400" />
              <h3 className="text-lg font-bold text-white">Multi-Run Statistical Variance & 95% Confidence Intervals</h3>
            </div>
            <span className="px-3 py-1 bg-cyan-500/20 text-cyan-400 border border-cyan-500/40 rounded-full font-mono text-xs font-bold">
              {experiment.totalTrials} EMPIRICAL RUNS
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 font-mono">
            <div className="p-4 bg-slate-950/80 rounded-xl border border-slate-800 space-y-1">
              <span className="text-xs text-slate-400">Mean Precision (± StdErr)</span>
              <p className="text-2xl font-extrabold text-emerald-400">{experiment.meanPrecision.toFixed(2)}%</p>
              <p className="text-[11px] text-slate-400">StdDev: ±{experiment.stdDevPrecision.toFixed(2)}%</p>
              <p className="text-[11px] text-emerald-400/90">95% CI: [{experiment.ci95PrecisionLower}%, {experiment.ci95PrecisionUpper}%]</p>
            </div>

            <div className="p-4 bg-slate-950/80 rounded-xl border border-slate-800 space-y-1">
              <span className="text-xs text-slate-400">Mean Recall / Detection</span>
              <p className="text-2xl font-extrabold text-emerald-400">{experiment.meanRecall.toFixed(2)}%</p>
              <p className="text-[11px] text-slate-400">StdDev: ±{experiment.stdDevRecall.toFixed(2)}%</p>
              <p className="text-[11px] text-emerald-400/90">95% CI: [{experiment.ci95RecallLower}%, {experiment.ci95RecallUpper}%]</p>
            </div>

            <div className="p-4 bg-slate-950/80 rounded-xl border border-slate-800 space-y-1">
              <span className="text-xs text-slate-400">Harmonic Mean (F1-Score)</span>
              <p className="text-2xl font-extrabold text-cyan-400">{experiment.meanF1Score.toFixed(2)}%</p>
              <p className="text-[11px] text-slate-400">StdDev: ±{experiment.stdDevF1Score.toFixed(2)}%</p>
              <p className="text-[11px] text-cyan-400/90">Optimal Balance</p>
            </div>

            <div className="p-4 bg-slate-950/80 rounded-xl border border-slate-800 space-y-1">
              <span className="text-xs text-slate-400">Target SLA Compliance</span>
              <p className="text-2xl font-extrabold text-purple-400">{experiment.meanSlaComplianceRate.toFixed(2)}%</p>
              <p className="text-[11px] text-slate-400">StdDev: ±{experiment.stdDevSlaComplianceRate.toFixed(2)}%</p>
              <p className="text-[11px] text-purple-400/90">Multi-Tier Compliance</p>
            </div>
          </div>
        </div>
      )}

      {/* Time-to-Remediation vs Stated SLA Targets (15 / 60 / 1440 min) */}
      {experiment && experiment.slaPerformances && (
        <div className="p-6 bg-slate-900/80 rounded-2xl border border-slate-800 space-y-6">
          <div className="flex justify-between items-center pb-3 border-b border-slate-800">
            <div className="flex items-center space-x-3">
              <Clock className="w-5 h-5 text-cyan-400" />
              <div>
                <h3 className="text-lg font-bold text-white">Time-to-Remediation vs Stated SLA Targets</h3>
                <p className="text-xs text-slate-400">Empirical MTTR and Variance across 15 min, 60 min, and 1440 min (24 hr) SLA Tiers</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 font-mono">
            {experiment.slaPerformances.map((s, idx) => (
              <div key={idx} className="p-5 bg-slate-950 rounded-xl border border-slate-800 space-y-3 relative overflow-hidden">
                <div className="flex justify-between items-center">
                  <span className="text-xs font-bold text-cyan-400">{s.slaCategory}</span>
                  <span className="px-2 py-0.5 bg-slate-900 text-slate-300 text-[10px] rounded border border-slate-800">
                    SLA: {s.targetSlaMinutes}m
                  </span>
                </div>

                <div>
                  <p className="text-xs text-slate-400">Observed Mean MTTR</p>
                  <p className="text-3xl font-extrabold text-white mt-1">
                    {s.observedMeanMttrMinutes} <span className="text-xs font-normal text-slate-400">mins</span>
                  </p>
                </div>

                <div className="space-y-1 text-[11px] text-slate-400 pt-2 border-t border-slate-800/80">
                  <div className="flex justify-between">
                    <span>MTTR Variance ($S^2$):</span>
                    <span className="text-slate-200 font-bold">{s.varianceMttr.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Std Deviation ($S$):</span>
                    <span className="text-slate-200 font-bold">±{s.stdDevMttr.toFixed(2)} min</span>
                  </div>
                  <div className="flex justify-between">
                    <span>SLA Target Met:</span>
                    <span className="text-emerald-400 font-bold">{s.slaCompliancePercentage.toFixed(1)}%</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Evaluated / Breaches:</span>
                    <span className="text-slate-300">{s.totalIssuesEvaluated} / {s.slaBreachesCount}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Grounded Error Analysis Matrix */}
      {experiment && experiment.errorAnalyses && (
        <div className="p-6 bg-slate-900/80 rounded-2xl border border-slate-800 space-y-6">
          <div className="flex justify-between items-center pb-3 border-b border-slate-800">
            <div className="flex items-center space-x-3">
              <Layers className="w-5 h-5 text-amber-400" />
              <div>
                <h3 className="text-lg font-bold text-white">Grounded Error Analysis & Threshold Rationale</h3>
                <p className="text-xs text-slate-400">Categorization of observed engine outputs across confidence thresholds ($\ge 0.75$, $0.30-0.75$, $0.10-0.30$)</p>
              </div>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs font-mono">
              <thead className="bg-slate-950 text-slate-400 uppercase text-[11px]">
                <tr>
                  <th className="p-3">Confidence Band</th>
                  <th className="p-3">Architectural Rationale</th>
                  <th className="p-3 text-center">Evaluated</th>
                  <th className="p-3 text-center">FP / FN</th>
                  <th className="p-3 text-center">Precision</th>
                  <th className="p-3">Remediation Strategy</th>
                  <th className="p-3">Observed Empirical Output</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800 text-slate-300">
                {experiment.errorAnalyses.map((e, idx) => (
                  <tr key={idx} className="hover:bg-slate-800/40 transition-colors">
                    <td className="p-3 font-bold text-white">
                      <span className={`px-2.5 py-1 rounded text-[11px] ${
                        idx === 0 ? 'bg-emerald-950 text-emerald-400 border border-emerald-800' :
                        idx === 1 ? 'bg-cyan-950 text-cyan-400 border border-cyan-800' :
                        'bg-amber-950 text-amber-400 border border-amber-800'
                      }`}>
                        {e.confidenceBand}
                      </span>
                    </td>
                    <td className="p-3 text-slate-300 font-sans max-w-xs">{e.rationale}</td>
                    <td className="p-3 text-center font-bold text-white">{e.issueCount}</td>
                    <td className="p-3 text-center font-bold text-emerald-400">{e.falsePositives} / {e.falseNegatives}</td>
                    <td className="p-3 text-center font-bold text-emerald-400">{e.precision.toFixed(1)}%</td>
                    <td className="p-3 font-sans font-semibold text-cyan-300">{e.remediationStrategy}</td>
                    <td className="p-3 text-slate-400 font-sans max-w-sm">{e.observedOutputDetails}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Direct Engine Comparison Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Baseline Card */}
        <div className="p-6 bg-slate-900/70 rounded-2xl border border-slate-800 space-y-4">
          <div className="flex justify-between items-center pb-3 border-b border-slate-800">
            <div>
              <h3 className="text-lg font-bold text-slate-200">Naive Single-Source Baseline</h3>
              <p className="text-xs text-slate-400 font-mono">Evaluates single application entitlements feed only</p>
            </div>
            <span className="px-3 py-1 bg-slate-800 text-slate-400 border border-slate-700 rounded-full font-mono text-xs font-bold">
              BASELINE
            </span>
          </div>

          {baseline ? (
            <div className="space-y-4 font-mono">
              <div className="grid grid-cols-2 gap-3 text-xs">
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800">
                  <p className="text-slate-400">Precision</p>
                  <p className="text-2xl font-extrabold text-amber-400">{baseline.precision.toFixed(1)}%</p>
                </div>

                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800">
                  <p className="text-slate-400">Recall / Detection</p>
                  <p className="text-2xl font-extrabold text-rose-400">{baseline.recall.toFixed(1)}%</p>
                </div>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs space-y-1">
                <div className="flex justify-between text-slate-300">
                  <span>True Positives (TP):</span>
                  <span className="font-bold text-white">{baseline.truePositives}</span>
                </div>
                <div className="flex justify-between text-slate-300">
                  <span>False Positives (FP):</span>
                  <span className="font-bold text-amber-400">{baseline.falsePositives}</span>
                </div>
                <div className="flex justify-between text-slate-300">
                  <span>False Negatives (FN):</span>
                  <span className="font-bold text-rose-400">{baseline.falseNegatives}</span>
                </div>
                <div className="flex justify-between text-slate-300 pt-1 border-t border-slate-800">
                  <span>Target SLA Compliance:</span>
                  <span className="font-bold text-slate-300">{baseline.targetSlaComplianceRate.toFixed(1)}%</span>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-slate-500 font-mono text-xs">Loading baseline...</div>
          )}
        </div>

        {/* Prototype Card */}
        <div className="p-6 bg-gradient-to-br from-slate-900/90 via-slate-900/80 to-cyan-950/40 rounded-2xl border border-cyan-500/40 space-y-4 shadow-xl shadow-cyan-950/20">
          <div className="flex justify-between items-center pb-3 border-b border-slate-800">
            <div>
              <h3 className="text-lg font-bold text-white">Improved Prototype Engine</h3>
              <p className="text-xs text-cyan-400 font-mono">Multi-source + Dynamic Confidence + Safety Gates</p>
            </div>
            <span className="px-3 py-1 bg-cyan-500/20 text-cyan-400 border border-cyan-500/40 rounded-full font-mono text-xs font-bold animate-pulse">
              PROTOTYPE
            </span>
          </div>

          {prototype ? (
            <div className="space-y-4 font-mono">
              <div className="grid grid-cols-2 gap-3 text-xs">
                <div className="p-3 bg-slate-950 rounded-xl border border-cyan-500/30">
                  <p className="text-slate-400">Precision</p>
                  <p className="text-2xl font-extrabold text-emerald-400">{prototype.precision.toFixed(1)}%</p>
                </div>

                <div className="p-3 bg-slate-950 rounded-xl border border-cyan-500/30">
                  <p className="text-slate-400">Recall / Detection</p>
                  <p className="text-2xl font-extrabold text-emerald-400">{prototype.recall.toFixed(1)}%</p>
                </div>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs space-y-1">
                <div className="flex justify-between text-slate-300">
                  <span>True Positives (TP):</span>
                  <span className="font-bold text-emerald-400">{prototype.truePositives}</span>
                </div>
                <div className="flex justify-between text-slate-300">
                  <span>False Positives (FP):</span>
                  <span className="font-bold text-emerald-400">{prototype.falsePositives}</span>
                </div>
                <div className="flex justify-between text-slate-300">
                  <span>False Negatives (FN):</span>
                  <span className="font-bold text-emerald-400">{prototype.falseNegatives}</span>
                </div>
                <div className="flex justify-between text-slate-300 pt-1 border-t border-slate-800">
                  <span>Target SLA Compliance:</span>
                  <span className="font-bold text-cyan-400">{prototype.targetSlaComplianceRate.toFixed(1)}%</span>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-slate-500 font-mono text-xs">Loading prototype metrics...</div>
          )}
        </div>
      </div>
    </div>
  );
};
