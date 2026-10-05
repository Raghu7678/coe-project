import React from 'react';
import { AlertTriangle, Server } from 'lucide-react';
import { DataSourceHealth, HealthState } from '../types';

interface BannerProps {
  healthList: DataSourceHealth[];
}

export const DataSourceHealthBanner: React.FC<BannerProps> = ({ healthList }) => {
  const degradedFeeds = healthList.filter(
    (h) => h.status !== 'AVAILABLE'
  );

  if (degradedFeeds.length === 0) {
    return null;
  }

  return (
    <div className="mb-6 bg-gradient-to-r from-amber-950/60 via-slate-900 to-amber-950/60 border border-amber-500/40 rounded-xl p-4 flex items-center justify-between text-amber-200 shadow-lg shadow-amber-950/30">
      <div className="flex items-center space-x-3">
        <div className="p-2 bg-amber-500/20 rounded-lg border border-amber-500/30">
          <AlertTriangle className="w-5 h-5 text-amber-400 animate-pulse" />
        </div>
        <div>
          <h4 className="text-sm font-bold text-amber-300">
            DATA FEED DEGRADATION DETECTED ({degradedFeeds.length} Feed{degradedFeeds.length > 1 ? 's' : ''})
          </h4>
          <p className="text-xs text-slate-300 font-mono mt-0.5">
            {degradedFeeds
              .map((f) => `${f.sourceName}: ${f.status} (${f.dataStalenessHours}h stale)`)
              .join(' | ')}
          </p>
        </div>
      </div>
      <div className="flex items-center space-x-2 text-xs font-mono bg-amber-950/80 px-3 py-1.5 rounded-lg border border-amber-500/30">
        <Server className="w-4 h-4 text-amber-400" />
        <span>Safety Gates Enforced</span>
      </div>
    </div>
  );
};
