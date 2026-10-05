import React, { useState, useEffect } from 'react';
import { ShieldCheck, Activity, Bell, AlertTriangle, Info, CheckCircle2, X } from 'lucide-react';
import { HealthState, NotificationAlert } from '../types';
import { api } from '../services/api';

interface NavbarProps {
  averageConfidence?: number;
  healthMap?: Record<string, HealthState>;
  onNavigate?: (tab: string) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ averageConfidence = 1.0, onNavigate }) => {
  const confidencePercent = Math.round(averageConfidence * 100);
  const [notifications, setNotifications] = useState<NotificationAlert[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  const fetchNotifications = async () => {
    try {
      const data = await api.getNotifications();
      setNotifications(data);
      setUnreadCount(data.filter(n => !n.read).length);
    } catch (err) {
      console.error('Failed to fetch notifications:', err);
    }
  };

  useEffect(() => {
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 8000);
    return () => clearInterval(interval);
  }, []);

  const handleAlertClick = (alert: NotificationAlert) => {
    if (onNavigate && alert.targetTab) {
      onNavigate(alert.targetTab);
    }
    setIsOpen(false);
  };

  return (
    <header className="h-16 border-b border-cyan-900/40 bg-slate-950/80 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-40">
      <div className="flex items-center space-x-3">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-600 via-blue-600 to-indigo-600 p-[1px] shadow-lg shadow-cyan-500/20">
          <div className="w-full h-full bg-slate-950 rounded-xl flex items-center justify-center">
            <ShieldCheck className="w-6 h-6 text-cyan-400" />
          </div>
        </div>
        <div>
          <h1 className="text-lg font-bold bg-gradient-to-r from-white via-slate-200 to-cyan-400 bg-clip-text text-transparent tracking-wide">
            JML ACCESS RECONCILIATION ENGINE
          </h1>
          <p className="text-xs text-slate-400 font-mono">ACCOUNTABLE APPROVALS • SLA TRACKING • EVIDENCE TRAIL</p>
        </div>
      </div>

      <div className="flex items-center space-x-6">
        <div className="flex items-center space-x-2 bg-slate-900/80 px-3 py-1.5 rounded-lg border border-slate-800">
          <Activity className={`w-4 h-4 ${confidencePercent >= 75 ? 'text-emerald-400 animate-pulse' : 'text-amber-400 animate-bounce'}`} />
          <span className="text-xs font-medium text-slate-300">Data Confidence:</span>
          <span className={`text-xs font-bold font-mono ${confidencePercent >= 75 ? 'text-emerald-400' : 'text-amber-400'}`}>
            {confidencePercent}%
          </span>
        </div>

        <div className="flex items-center space-x-2 bg-slate-900/80 px-3 py-1.5 rounded-lg border border-slate-800">
          <div className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
          <span className="text-xs font-mono text-cyan-400">SYSTEM: ONLINE</span>
        </div>

        {/* Real-time Notification Alert Bell */}
        <div className="relative">
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="relative p-2 text-slate-400 hover:text-white rounded-lg hover:bg-slate-900 transition-colors focus:outline-none"
            title="Real-time System Alerts"
          >
            <Bell className="w-5 h-5 text-cyan-400" />
            {unreadCount > 0 && (
              <span className="absolute top-1 right-1 px-1.5 py-0.5 text-[10px] font-bold font-mono bg-rose-500 text-white rounded-full animate-pulse shadow-lg shadow-rose-500/50">
                {unreadCount}
              </span>
            )}
          </button>

          {isOpen && (
            <div className="absolute right-0 mt-2 w-96 bg-slate-900 border border-cyan-800/60 rounded-xl shadow-2xl z-50 overflow-hidden backdrop-blur-xl">
              <div className="p-3 bg-slate-950 border-b border-slate-800 flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Bell className="w-4 h-4 text-cyan-400" />
                  <span className="text-xs font-bold text-white tracking-wider">REAL-TIME SLA & ANOMALY ALERTS</span>
                </div>
                <button onClick={() => setIsOpen(false)} className="text-slate-400 hover:text-white">
                  <X className="w-4 h-4" />
                </button>
              </div>

              <div className="max-h-80 overflow-y-auto divide-y divide-slate-800">
                {notifications.length === 0 ? (
                  <div className="p-4 text-center text-xs text-slate-500">No active alerts. All SLAs nominal.</div>
                ) : (
                  notifications.map((n) => (
                    <div
                      key={n.id}
                      onClick={() => handleAlertClick(n)}
                      className="p-3 hover:bg-slate-800/80 cursor-pointer transition-colors flex items-start space-x-3"
                    >
                      {n.severity === 'CRITICAL' && <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />}
                      {n.severity === 'WARNING' && <AlertTriangle className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />}
                      {n.severity === 'INFO' && <Info className="w-5 h-5 text-cyan-400 shrink-0 mt-0.5" />}
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <p className="text-xs font-bold text-slate-200">{n.title}</p>
                          <span className="text-[10px] text-slate-500 font-mono">
                            {new Date(n.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                          </span>
                        </div>
                        <p className="text-xs text-slate-400 mt-1 leading-snug">{n.message}</p>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
