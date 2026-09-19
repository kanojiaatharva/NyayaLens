import {
  ApiResponse,
  DocumentUploadResponse,
  LegalDocument,
  AnalysisResult,
  Claim,
  ComparisonResult,
  ChatAnswerResponse,
  ActionPathResult,
  LegalResource,
} from '../types';

const API_BASE = '/api/v1';

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${API_BASE}${endpoint}`;
  try {
    const res = await fetch(url, {
      ...options,
      headers: {
        'Accept': 'application/json',
        ...(options.headers || {}),
      },
    });

    const json: ApiResponse<T> = await res.json();

    if (!res.ok || !json.success) {
      const errorMsg = json.error?.message || `Request failed with status ${res.status}`;
      throw new Error(errorMsg);
    }

    return json.data;
  } catch (err: unknown) {
    if (err instanceof Error) {
      throw err;
    }
    throw new Error('An unexpected network error occurred while connecting to the NyayaLens service.');
  }
}

export const api = {
  getHealth: () => request<Record<string, unknown>>('/health'),

  uploadDocument: async (file: File, redactPii = true): Promise<DocumentUploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);
    return request<DocumentUploadResponse>(`/documents?redactPii=${redactPii}`, {
      method: 'POST',
      body: formData,
    });
  },

  loadDemoDocument: (scenario: string, redactPii = true): Promise<DocumentUploadResponse> => {
    return request<DocumentUploadResponse>(`/documents/demo/${scenario}?redactPii=${redactPii}`, {
      method: 'POST',
    });
  },

  getDocument: (id: string): Promise<LegalDocument> => {
    return request<LegalDocument>(`/documents/${id}`);
  },

  listDocuments: (): Promise<DocumentUploadResponse[]> => {
    return request<DocumentUploadResponse[]>('/documents');
  },

  deleteDocument: (id: string): Promise<void> => {
    return request<void>(`/documents/${id}`, { method: 'DELETE' });
  },

  deleteAllSessionData: (): Promise<void> => {
    return request<void>('/documents', { method: 'DELETE' });
  },

  analyzeDocument: (id: string): Promise<AnalysisResult> => {
    return request<AnalysisResult>(`/documents/${id}/analyze`, {
      method: 'POST',
    });
  },

  getDocumentRisks: (id: string): Promise<Claim[]> => {
    return request<Claim[]>(`/documents/${id}/risks`);
  },

  compareDocuments: (documentAId: string, documentBId: string): Promise<ComparisonResult> => {
    return request<ComparisonResult>('/compare', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ documentAId, documentBId }),
    });
  },

  askQuestion: (documentId: string, question: string): Promise<ChatAnswerResponse> => {
    return request<ChatAnswerResponse>('/questions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ documentId, question }),
    });
  },

  getActionPath: (documentId: string): Promise<ActionPathResult> => {
    return request<ActionPathResult>('/actionpath', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ documentId }),
    });
  },

  getLegalResources: (): Promise<LegalResource[]> => {
    return request<LegalResource[]>('/legal-resources');
  },
};
