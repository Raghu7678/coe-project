import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertOctagon, RefreshCw, RotateCcw, ChevronDown, ChevronRight, Copy, Check } from 'lucide-react';

interface ErrorBoundaryProps {
  /** Child components wrapped by this error boundary */
  children: ReactNode;
  /** Optional custom title for the fallback UI */
  fallbackTitle?: string;
  /** Optional custom description */
  fallbackMessage?: string;
  /** Optional callback invoked when the user clicks 'Try Again' */
  onReset?: () => void;
  /** If true, renders a compact inline error card instead of a full-page container */
  isolate?: boolean;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
  showDetails: boolean;
  copied: boolean;
  errorId: string;
}

/**
 * Enterprise React Error Boundary Component.
 * 
 * Catches JavaScript errors anywhere in the child component tree,
 * logs the technical telemetry, and renders a fallback cyber-glassmorphic UI
 * instead of crashing the entire application to a blank screen.
 * 
 * Supports:
 * - Isolation mode (inline card vs full view)
 * - Self-healing reset ("Try Again" button)
 * - Page-level recovery ("Reload Application" / "Reset to Home")
 * - Developer diagnostics (expandable stack trace and copy-to-clipboard)
 */
export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  public state: ErrorBoundaryState = {
    hasError: false,
    error: null,
    errorInfo: null,
    showDetails: false,
    copied: false,
    errorId: ''
  };

  public static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    // Update state so the next render will show the fallback UI
    const errorId = `ERR-${Date.now().toString(36).toUpperCase()}-${Math.random().toString(36).substring(2, 6).toUpperCase()}`;
    return {
      hasError: true,
      error,
      errorId
    };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    // Log technical error telemetry for audit and diagnosis
    console.error('ErrorBoundary caught an unhandled component error:', error, errorInfo);
    this.setState({ errorInfo });
  }

  private handleReset = (): void => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
      showDetails: false,
      copied: false,
      errorId: ''
    });
    if (this.props.onReset) {
      this.props.onReset();
    }
  };

  private handleReload = (): void => {
    window.location.reload();
  };

  private handleCopyDiagnostics = (): void => {
    const { error, errorInfo, errorId } = this.state;
    const diagnosticPayload = {
      errorId,
      timestamp: new Date().toISOString(),
      name: error?.name,
      message: error?.message,
      stack: error?.stack,
      componentStack: errorInfo?.componentStack
    };

    navigator.clipboard.writeText(JSON.stringify(diagnosticPayload, null, 2)).then(() => {
      this.setState({ copied: true });
      setTimeout(() => this.setState({ copied: false }), 2500);
    });
  };

  public render(): ReactNode {
    if (this.state.hasError) {
      const { fallbackTitle, fallbackMessage, isolate } = this.props;
      const { error, errorInfo, showDetails, copied, errorId } = this.state;

      const title = fallbackTitle || 'Component Rendering Interrupted';
      const message = fallbackMessage || 'An unexpected rendering anomaly occurred within this interface boundary. Core system operations and navigation remain operational.';

      if (isolate) {
        return (
          <div className="p-4 my-4 bg-red-950/40 border border-red-500/40 rounded-xl text-red-200 font-mono text-sm space-y-3">
            <div className="flex items-center space-x-2 text-red-400">
              <AlertOctagon className="w-5 h-5 flex-shrink-0" />
              <span className="font-bold">{title}</span>
              <span className="text-xs text-red-400/60 ml-auto">{errorId}</span>
            </div>
            <p className="text-xs text-slate-300">{message}</p>
            {error && (
              <p className="text-xs bg-red-950/80 p-2 rounded border border-red-800/40 text-red-300 truncate">
                {error.name}: {error.message}
              </p>
            )}
            <div className="flex items-center space-x-2 pt-1">
              <button
                onClick={this.handleReset}
                className="px-3 py-1 bg-red-900/60 hover:bg-red-800/80 text-white rounded text-xs flex items-center space-x-1"
              >
                <RefreshCw className="w-3 h-3" />
                <span>Retry Component</span>
              </button>
            </div>
          </div>
        );
      }

      return (
        <div className="min-h-[420px] p-8 flex flex-col items-center justify-center bg-slate-900/60 border border-red-500/30 rounded-2xl backdrop-blur-md shadow-2xl space-y-6 my-6 text-center max-w-4xl mx-auto">
          <div className="p-4 bg-red-950/80 border border-red-500/50 rounded-2xl text-red-400 shadow-lg shadow-red-950/50">
            <AlertOctagon className="w-12 h-12 animate-pulse" />
          </div>

          <div className="space-y-2 max-w-xl">
            <div className="inline-block px-3 py-1 bg-red-900/40 border border-red-500/30 text-red-400 text-xs font-mono rounded-full mb-1">
              FAULT ISOLATION BOUNDARY • {errorId}
            </div>
            <h2 className="text-2xl font-bold text-white tracking-wide">{title}</h2>
            <p className="text-sm text-slate-400 leading-relaxed">{message}</p>
          </div>

          {error && (
            <div className="w-full max-w-2xl bg-slate-950/90 border border-slate-800 rounded-xl p-3 text-left font-mono text-xs text-red-300 break-words">
              <span className="text-red-500 font-bold">{error.name}:</span> {error.message}
            </div>
          )}

          <div className="flex flex-wrap gap-3 justify-center">
            <button
              onClick={this.handleReset}
              className="px-5 py-2.5 bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500 text-white font-medium text-sm rounded-xl flex items-center space-x-2 shadow-lg shadow-cyan-950/50 transition-all"
            >
              <RefreshCw className="w-4 h-4" />
              <span>Retry Component</span>
            </button>

            <button
              onClick={this.handleReload}
              className="px-5 py-2.5 bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 font-medium text-sm rounded-xl flex items-center space-x-2 transition-all"
            >
              <RotateCcw className="w-4 h-4" />
              <span>Reload Application</span>
            </button>

            <button
              onClick={this.handleCopyDiagnostics}
              className="px-4 py-2.5 bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-300 font-medium text-sm rounded-xl flex items-center space-x-2 transition-all"
            >
              {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
              <span>{copied ? 'Copied Telemetry' : 'Copy Diagnostics'}</span>
            </button>
          </div>

          {/* Collapsible Technical Details for Audit / Debugging */}
          <div className="w-full max-w-2xl pt-2">
            <button
              onClick={() => this.setState({ showDetails: !showDetails })}
              className="flex items-center space-x-1 text-xs text-slate-500 hover:text-slate-300 transition-colors mx-auto font-mono"
            >
              {showDetails ? <ChevronDown className="w-4 h-4" /> : <ChevronRight className="w-4 h-4" />}
              <span>{showDetails ? 'Hide Technical Stack Trace' : 'Show Technical Stack Trace'}</span>
            </button>

            {showDetails && (
              <div className="mt-3 p-4 bg-slate-950 border border-slate-800 rounded-xl text-left font-mono text-[11px] text-slate-400 overflow-x-auto max-h-60 space-y-2">
                <div>
                  <span className="text-cyan-400 font-bold">Error Stack:</span>
                  <pre className="mt-1 whitespace-pre-wrap text-slate-400">{error?.stack || 'No stack trace available.'}</pre>
                </div>
                {errorInfo && (
                  <div className="pt-2 border-t border-slate-900">
                    <span className="text-yellow-400 font-bold">Component Hierarchy:</span>
                    <pre className="mt-1 whitespace-pre-wrap text-slate-500">{errorInfo.componentStack}</pre>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
