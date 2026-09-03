import React, { useState, useEffect } from 'react';
import { EvaluationMetrics } from '../types';
import { api } from '../services/api';
import { BarChart3, Zap, Layers, CheckCircle2, AlertCircle, RefreshCw } from 'lucide-react';

export const EvaluationMetricsPage: React.FC = () => {
  const [metrics, setMetrics] = useState<EvaluationMetrics | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchMetrics = async () => {
    try {
      setLoading(true);
      const res = await api.runEvaluation();
      setMetrics(res);
    } catch (err: any) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMetrics();
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
            Baseline vs Improved Prototype Evaluation
          </h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
            Empirical benchmark comparing naive single-source baseline against the multi-source resilient reconciliation engine.
          </p>
        </div>
        <button onClick={fetchMetrics} disabled={loading} className="btn btn-primary">
          <RefreshCw size={15} className={loading ? 'animate-spin' : ''} />
          {loading ? 'Running Experiment...' : 'Re-Run Evaluation Experiment'}
        </button>
      </div>

      {!metrics ? (
        <div style={{ padding: '2rem', color: 'var(--text-muted)' }}>Executing benchmark experiment...</div>
      ) : (
        <>
          {/* Comparative Metrics Grid */}
          <div className="grid-2">
            {/* Baseline Card */}
            <div className="glass-card" style={{ borderTop: '4px solid #94a3b8' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
                <Layers size={20} color="#94a3b8" />
                <h3 style={{ fontSize: '1.15rem', fontWeight: 800, color: 'white' }}>Baseline Engine</h3>
              </div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '1.25rem' }}>
                Naive algorithm: Uses ONLY HR Role + Application Entitlements. No directory, approval history, or resilience logic.
              </p>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '1rem', marginBottom: '1.25rem' }}>
                <div style={{ padding: '0.75rem', background: 'rgba(255,255,255,0.03)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>PRECISION</div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#f87171' }}>{(metrics.baselinePrecision * 100).toFixed(0)}%</div>
                </div>
                <div style={{ padding: '0.75rem', background: 'rgba(255,255,255,0.03)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>RECALL</div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#fde047' }}>{(metrics.baselineRecall * 100).toFixed(0)}%</div>
                </div>
                <div style={{ padding: '0.75rem', background: 'rgba(255,255,255,0.03)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>TRUE POSITIVES</div>
                  <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'white' }}>{metrics.baselineTruePositives} / {metrics.groundTruthIssuesCount}</div>
                </div>
                <div style={{ padding: '0.75rem', background: 'rgba(255,255,255,0.03)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>FALSE NEGATIVES</div>
                  <div style={{ fontSize: '1.25rem', fontWeight: 700, color: '#f87171' }}>{metrics.baselineFalseNegatives} Missed</div>
                </div>
              </div>

              <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                Target Time SLA Compliance: <strong style={{ color: '#fde047' }}>{(metrics.baselineTargetComplianceRate * 100).toFixed(0)}%</strong>
              </div>
            </div>

            {/* Prototype Card */}
            <div className="glass-card" style={{ borderTop: '4px solid var(--accent-cyan)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
                <Zap size={20} color="var(--accent-cyan)" />
                <h3 style={{ fontSize: '1.15rem', fontWeight: 800, color: 'white' }}>Improved Prototype Engine</h3>
              </div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '1.25rem' }}>
                Multi-source algorithm: Integrates HR + Directory + Entitlements + Approvals + Confidence Scoring + Accountable Approvals.
              </p>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '1rem', marginBottom: '1.25rem' }}>
                <div style={{ padding: '0.75rem', background: 'rgba(6, 182, 212, 0.1)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>PRECISION</div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#34d399' }}>{(metrics.prototypePrecision * 100).toFixed(0)}%</div>
                </div>
                <div style={{ padding: '0.75rem', background: 'rgba(6, 182, 212, 0.1)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>RECALL</div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#34d399' }}>{(metrics.prototypeRecall * 100).toFixed(0)}%</div>
                </div>
                <div style={{ padding: '0.75rem', background: 'rgba(6, 182, 212, 0.1)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>TRUE POSITIVES</div>
                  <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'white' }}>{metrics.prototypeTruePositives} / {metrics.groundTruthIssuesCount}</div>
                </div>
                <div style={{ padding: '0.75rem', background: 'rgba(6, 182, 212, 0.1)', borderRadius: '8px' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>FALSE NEGATIVES</div>
                  <div style={{ fontSize: '1.25rem', fontWeight: 700, color: '#34d399' }}>{metrics.prototypeFalseNegatives} Missed</div>
                </div>
              </div>

              <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                Target Time SLA Compliance: <strong style={{ color: '#34d399' }}>{(metrics.prototypeTargetComplianceRate * 100).toFixed(0)}%</strong>
              </div>
            </div>
          </div>

          {/* Error Analysis & Rationale */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'white', marginBottom: '1rem' }}>
              Error Analysis & Technical Comparison
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {metrics.errorAnalysisNotes.map((note, idx) => (
                <div key={idx} style={{ display: 'flex', alignItems: 'flex-start', gap: '0.75rem', padding: '0.75rem', background: 'rgba(255,255,255,0.03)', borderRadius: '8px', fontSize: '0.875rem' }}>
                  <CheckCircle2 size={18} color="var(--accent-cyan)" style={{ flexShrink: 0, marginTop: '0.1rem' }} />
                  <span style={{ color: 'var(--text-primary)' }}>{note}</span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
};
