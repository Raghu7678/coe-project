export type EmploymentStatus = 'ACTIVE' | 'LEFT' | 'ON_LEAVE';

export type PermissionLevel = 'NONE' | 'READ' | 'USER' | 'WRITE' | 'ADMIN';

export type IssueType = 
  | 'ORPHANED_ACCESS' 
  | 'EXCESSIVE_ACCESS' 
  | 'UNAPPROVED_ACCESS' 
  | 'MISSING_ACCESS' 
  | 'ROLE_CHANGE_ACCESS_CONFLICT';

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type HealthState = 'AVAILABLE' | 'DELAYED' | 'STALE' | 'UNAVAILABLE';

export type RemediationStatus = 
  | 'RECOMMENDED' 
  | 'PENDING_REVIEW' 
  | 'APPROVED' 
  | 'REJECTED' 
  | 'EXECUTED' 
  | 'FAILED' 
  | 'ROLLED_BACK';

export type RemediationActionType = 
  | 'REMOVE_ACCESS' 
  | 'REDUCE_PERMISSION' 
  | 'ADD_ACCESS' 
  | 'ADD_TO_GROUP' 
  | 'REMOVE_FROM_GROUP';

export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED';

export type EventType = 
  | 'RECONCILIATION_STARTED' 
  | 'DATA_SOURCE_UNAVAILABLE' 
  | 'DATA_SOURCE_STALE' 
  | 'ISSUE_DETECTED' 
  | 'REMEDIATION_CREATED' 
  | 'REVIEW_REQUESTED' 
  | 'APPROVAL_GRANTED' 
  | 'APPROVAL_REJECTED' 
  | 'ACCESS_REMOVED' 
  | 'ACCESS_REDUCED' 
  | 'ROLLBACK_EXECUTED' 
  | 'DATA_SOURCE_HEALTH_CHANGED';

export type EngineType = 'PROTOTYPE' | 'BASELINE';

export interface Employee {
  employeeId: string;
  userId: string;
  name: string;
  email: string;
  department: string;
  currentRole: string;
  previousRole?: string;
  employmentStatus: EmploymentStatus;
  joinDate: string;
  lastRoleChangeDate?: string;
  lastUpdated: string;
}

export interface RolePolicy {
  id?: number;
  roleName: string;
  applicationName: string;
  expectedPermission: PermissionLevel;
  required: boolean;
}

export interface DataSourceHealth {
  sourceName: string;
  status: HealthState;
  lastUpdated: string;
  freshness: string;
  recordsCount: number;
}

export interface ReconciliationIssue {
  issueId: string;
  userId: string;
  employeeName: string;
  currentRole: string;
  issueType: IssueType;
  applicationName: string;
  expectedAccess?: PermissionLevel;
  actualAccess?: PermissionLevel;
  riskLevel: RiskLevel;
  confidenceScore: number;
  engineType: EngineType;
  recommendedAction: RemediationActionType;
  status: string;
  detectedAt: string;
  remediatedAt?: string;
  remediationTimeMinutes?: number;
  targetTimeMinutes?: number;
  metTarget?: boolean;
}

export interface RemediationAction {
  remediationId: string;
  issueId: string;
  userId: string;
  applicationName: string;
  actionType: RemediationActionType;
  previousState: string;
  proposedState: string;
  status: RemediationStatus;
  requestedBy: string;
  approvedBy?: string;
  reviewerComment?: string;
  createdAt: string;
  executedAt?: string;
  rolledBackAt?: string;
  rollbackReason?: string;
}

export interface AuditEvent {
  auditId: string;
  timestamp: string;
  eventType: EventType;
  userId: string;
  actor: string;
  action: string;
  previousState?: string;
  newState?: string;
  reason?: string;
  relatedIssueId?: string;
  relatedApprovalId?: string;
  dataSourcesUsed?: string;
}

export interface DashboardSummary {
  totalUsers: number;
  activeIssues: number;
  criticalIssues: number;
  orphanedAccessCount: number;
  excessiveAccessCount: number;
  unapprovedAccessCount: number;
  missingAccessCount: number;
  pendingApprovalsCount: number;
  targetCompliancePercentage: number;
  avgRemediationTimeMinutes: number;
  dataSources: DataSourceHealth[];
}

export interface EvaluationMetrics {
  groundTruthIssuesCount: number;
  baselineDetectedCount: number;
  baselineTruePositives: number;
  baselineFalsePositives: number;
  baselineFalseNegatives: number;
  baselinePrecision: number;
  baselineRecall: number;
  baselineDetectionRate: number;
  baselineTargetComplianceRate: number;
  prototypeDetectedCount: number;
  prototypeTruePositives: number;
  prototypeFalsePositives: number;
  prototypeFalseNegatives: number;
  prototypePrecision: number;
  prototypeRecall: number;
  prototypeDetectionRate: number;
  prototypeTargetComplianceRate: number;
  errorAnalysisNotes: string[];
}
