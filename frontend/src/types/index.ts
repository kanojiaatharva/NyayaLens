export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type VerificationStatus = 'VERIFIED' | 'INFERRED' | 'NOT_FOUND' | 'NEEDS_REVIEW';

export interface Evidence {
  id: string;
  documentId: string;
  documentName: string;
  pageNumber: number;
  section: string;
  clause: string;
  excerpt: string;
  sourceType: string;
  confidence: number;
}

export interface Claim {
  id: string;
  claimText: string;
  category: string;
  severity: Severity;
  status: VerificationStatus;
  confidence: number;
  evidenceList: Evidence[];
  explanation: string;
  lawyerQuestion: string;
}

export interface DocumentPage {
  pageNumber: number;
  text: string;
}

export interface DocumentSection {
  id: string;
  clauseNumber: string;
  title: string;
  pageNumber: number;
  content: string;
}

export interface LegalDocument {
  id: string;
  filename: string;
  fileType: string;
  fileSize: number;
  uploadedAt: string;
  pageCount: number;
  rawText: string;
  sanitizedText: string;
  pages: DocumentPage[];
  sections: DocumentSection[];
  piiSummary: Record<string, number>;
  piiRedacted: boolean;
}

export interface DocumentUploadResponse {
  id: string;
  filename: string;
  fileType: string;
  fileSize: number;
  pageCount: number;
  sectionCount: number;
  uploadedAt: string;
  piiSummary: Record<string, number>;
  piiRedacted: boolean;
}

export interface PartyInfo {
  name: string;
  role: string;
}

export interface ObligationItem {
  party: string;
  description: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface DeadlineItem {
  description: string;
  dateOrDuration: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface FinancialTermItem {
  type: string;
  amount: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface TerminationItem {
  description: string;
  noticePeriod: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface RenewalItem {
  description: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface DisputeResolutionItem {
  method: string;
  venue: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface UnusualClauseItem {
  title: string;
  explanation: string;
  clause: string;
  page: number;
  excerpt: string;
}

export interface AnalysisResult {
  documentId: string;
  documentType: string;
  plainSummary: string;
  parties: PartyInfo[];
  effectiveDate: string;
  governingLaw: string;
  jurisdiction: string;
  obligations: ObligationItem[];
  deadlines: DeadlineItem[];
  financialTerms: FinancialTermItem[];
  terminationConditions: TerminationItem[];
  renewalConditions: RenewalItem[];
  disputeResolution: DisputeResolutionItem;
  unusualClauses: UnusualClauseItem[];
  missingOrAmbiguousInformation: string[];
  risks: Claim[];
  analyzedAt: string;
  modelUsed: string;
}

export interface ClauseDelta {
  id: string;
  clauseTopic: string;
  changeType: 'ADDED' | 'REMOVED' | 'MODIFIED' | 'UNCHANGED';
  severity: Severity;
  docAClause: string;
  docAPage: number;
  docAExcerpt: string;
  docBClause: string;
  docBPage: number;
  docBExcerpt: string;
  impactAssessment: string;
  recommendation: string;
}

export interface ComparisonResult {
  id: string;
  documentAId: string;
  documentAName: string;
  documentBId: string;
  documentBName: string;
  comparisonSummary: string;
  overallImpact: Severity;
  deltas: ClauseDelta[];
  comparedAt: string;
  modelUsed: string;
}

export interface ChatAnswerResponse {
  directAnswer: string;
  status: VerificationStatus;
  confidence: number;
  evidence: Evidence[];
  whatWasNotFound: string;
  suggestedNextQuestions: string[];
  legalSafetyNote: string;
  modelUsed: string;
}

export interface ActionStep {
  stepNumber: number;
  title: string;
  action: string;
  category: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
}

export interface LawyerQuestion {
  question: string;
  context: string;
  relevantClause: string;
}

export interface OfficialResourceItem {
  name: string;
  description: string;
  officialUrl: string;
}

export interface ActionPathResult {
  id: string;
  documentId: string;
  situationOverview: string;
  checklist: ActionStep[];
  questionsToAskLawyer: LawyerQuestion[];
  recommendedOfficialResources: OfficialResourceItem[];
  generatedAt: string;
  modelUsed: string;
}

export interface LegalResource {
  id: string;
  name: string;
  category: string;
  description: string;
  officialUrl: string;
  authorityLevel: string;
  keyLegislationCovered: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: {
    status: number;
    code: string;
    message: string;
  };
  timestamp: string;
  requestId: string;
}
