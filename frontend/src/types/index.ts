export type ContractorCategory =
  | 'IT_SERVICES'
  | 'CONSTRUCTION'
  | 'LOGISTICS'
  | 'SECURITY_SERVICES'
  | 'FACILITIES_MANAGEMENT';

export type RiskTier = 'TIER_1_CRITICAL' | 'TIER_2_HIGH' | 'TIER_3_MEDIUM';

export type QualificationStatus =
  | 'QUALIFIED'
  | 'CONDITIONALLY_QUALIFIED'
  | 'PENDING_REVIEW'
  | 'SUSPENDED'
  | 'DISQUALIFIED';

export type AnomalyType =
  | 'HIGH_EMR_SAFETY_VIOLATION'
  | 'EXPIRED_INSURANCE_DEFICIT'
  | 'FINANCIAL_INSOLVENCY_RISK'
  | 'SANCTION_WATCHLIST_MATCH'
  | 'MISSING_SECURITY_CERTIFICATION';

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type HealthState = 'AVAILABLE' | 'STALE' | 'DELAYED' | 'UNAVAILABLE';

export type RemediationStatus =
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'EXECUTED'
  | 'REJECTED'
  | 'ROLLED_BACK';

export interface Contractor {
  id: number;
  vendorCode: string;
  companyName: string;
  category: ContractorCategory;
  riskTier: RiskTier;
  status: QualificationStatus;
  primaryContact: string;
  contactEmail: string;
  contractValueUsd: number;
  onboardedAt: string;
  lastEvaluatedAt: string;
}

export interface ContractorRiskAnomaly {
  id: number;
  vendorCode: string;
  companyName: string;
  anomalyType: AnomalyType;
  riskLevel: RiskLevel;
  compositeRiskScore: number;
  dataConfidenceScore: number;
  description: string;
  evidenceDetails: string;
  slaTargetMinutes: number;
  detectionEngine: 'PROTOTYPE' | 'BASELINE';
  safetyGateTriggered: boolean;
  detectedAt: string;
}

export interface QualificationAction {
  id: number;
  anomalyId?: number;
  issueId?: number;
  username?: string;
  fullName?: string;
  vendorCode?: string;
  companyName?: string;
  actionType: string;
  appName?: string;
  status: RemediationStatus;
  previousQualificationStatus?: QualificationStatus;
  targetQualificationStatus?: QualificationStatus;
  previousPermissionLevel?: string;
  targetPermissionLevel?: string;
  rationale: string;
  initiatedBy: string;
  approvedBy?: string;
  createdAt: string;
  executedAt?: string;
  rolledBackAt?: string;
  stakeholderValidated?: boolean;
  validatedBy?: string;
  validationNotes?: string;
  validatedAt?: string;
}

export interface DataSourceHealth {
  id: number;
  sourceName: string;
  status: HealthState;
  latencyMs: number;
  dataStalenessHours: number;
  lastSyncTime: string;
}

export interface AuditEvent {
  id: number;
  eventType: string;
  actor: string;
  targetEntity: string;
  details: string;
  timestamp: string;
}

export interface DashboardSummary {
  totalContractors: number;
  qualifiedContractors: number;
  pendingReviewContractors: number;
  highRiskAnomaliesCount: number;
  pendingApprovalsCount: number;
  executedRemediationsCount: number;
  averageDataConfidence: number;
  dataSourceHealthMap: Record<string, HealthState>;
}

export interface EvaluationMetrics {
  engineName: string;
  groundTruthCount: number;
  detectedIssuesCount: number;
  truePositives: number;
  falsePositives: number;
  falseNegatives: number;
  precision: number;
  recall: number;
  detectionRate: number;
  targetSlaComplianceRate: number;
}

export interface SlaPerformance {
  slaCategory: string;
  targetSlaMinutes: number;
  observedMeanMttrMinutes: number;
  varianceMttr: number;
  stdDevMttr: number;
  slaCompliancePercentage: number;
  totalIssuesEvaluated: number;
  slaBreachesCount: number;
}

export interface ConfidenceErrorAnalysis {
  confidenceBand: string;
  rationale: string;
  issueCount: number;
  falsePositives: number;
  falseNegatives: number;
  precision: number;
  remediationStrategy: string;
  observedOutputDetails: string;
}

export interface ExperimentResult {
  engineName: string;
  totalTrials: number;
  meanPrecision: number;
  stdDevPrecision: number;
  stdErrPrecision: number;
  ci95PrecisionLower: number;
  ci95PrecisionUpper: number;
  meanRecall: number;
  stdDevRecall: number;
  stdErrRecall: number;
  ci95RecallLower: number;
  ci95RecallUpper: number;
  meanF1Score: number;
  stdDevF1Score: number;
  meanSlaComplianceRate: number;
  stdDevSlaComplianceRate: number;
  slaPerformances: SlaPerformance[];
  errorAnalyses: ConfidenceErrorAnalysis[];
}

export interface NotificationAlert {
  id: string;
  title: string;
  message: string;
  severity: 'CRITICAL' | 'WARNING' | 'INFO';
  targetTab: string;
  referenceId?: number;
  timestamp: string;
  read: boolean;
}

export interface RiskThresholdPolicy {
  id?: number;
  policyName: string;
  maxAllowedEmr: number;
  maxAllowedTrir: number;
  minInsuranceLimitMillion: number;
  minCreditScore: number;
  safetyWeight: number;
  financialWeight: number;
  insuranceWeight: number;
  sanctionWeight: number;
  confidenceSafetyGateThreshold: number;
}
