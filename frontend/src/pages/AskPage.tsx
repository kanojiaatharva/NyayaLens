import { useState } from 'react';
import {
  MessageCircleQuestion,
  Send,
  Scale,
  AlertCircle,
  HelpCircle,
  FileText,
  Sparkles,
  RefreshCw,
} from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { ChatAnswerResponse, Evidence, DocumentUploadResponse } from '../types';

export default function AskPage() {
  const { currentDocument, setCurrentDocument, documents, setDocuments, t } = useApp();

  const [question, setQuestion] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<ChatAnswerResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Suggested questions based on typical legal queries
  const suggestions = [
    'What is my notice period for resignation or termination?',
    'Can the landlord increase rent before the 11-month lease expires?',
    'Is there a non-compete restriction and what is its geographic scope?',
    'What are my financial liabilities if I terminate early?',
    'Which city has exclusive jurisdiction in case of a dispute?',
    'Does the agreement automatically renew unless cancelled in writing?',
  ];

  const handleAsk = async (queryText?: string) => {
    const q = (queryText || question).trim();
    if (!q) return;
    if (!currentDocument) {
      setError('Please select or upload a document first.');
      return;
    }

    setQuestion(q);
    setLoading(true);
    setError(null);

    try {
      const res = await api.askQuestion(currentDocument.id, q);
      setResponse(res);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to query document.');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectDoc = async (docId: string) => {
    try {
      const doc = await api.getDocument(docId);
      setCurrentDocument(doc);
      setResponse(null);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load document.');
    }
  };

  const handleQuickDemo = async (scenario: string) => {
    setLoading(true);
    setError(null);
    try {
      const uploadRes = await api.loadDemoDocument(scenario);
      setDocuments(prev => [...prev, uploadRes]);
      const fullDoc = await api.getDocument(uploadRes.id);
      setCurrentDocument(fullDoc);
      setResponse(null);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load demo.');
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'VERIFIED':
        return <span className="badge badge--verified">✓ VERIFIED IN TEXT</span>;
      case 'INFERRED':
        return <span className="badge badge--inferred">ℹ INFERRED FROM CONTEXT</span>;
      case 'NOT_FOUND':
        return <span className="badge badge--not-found">✕ NOT FOUND IN DOCUMENT</span>;
      default:
        return <span className="badge badge--needs-review">⚠ NEEDS REVIEW</span>;
    }
  };

  return (
    <div style={{ maxWidth: 900, margin: '0 auto' }}>
      <div className="section-header">
        <MessageCircleQuestion className="section-header__icon" size={24} />
        <div>
          <h2>{t.nav.ask}</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
            Query your contract directly. Every answer is grounded in exact clauses, with a transparent audit of what the document does NOT contain.
          </p>
        </div>
      </div>

      {error && (
        <div className="error-message" style={{ marginBottom: 16 }}>
          <AlertCircle size={16} style={{ display: 'inline', marginRight: 6, verticalAlign: 'text-bottom' }} />
          {error}
        </div>
      )}

      {/* Document Selector Header */}
      <div className="card" style={{ marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <FileText size={18} color="var(--color-accent)" />
            <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>Active Document:</span>
            {currentDocument ? (
              <strong style={{ fontSize: '0.9rem', color: 'var(--color-accent)' }}>{currentDocument.filename}</strong>
            ) : (
              <span style={{ color: 'var(--color-text-tertiary)', fontSize: '0.85rem' }}>No document selected</span>
            )}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            {documents.length > 0 && (
              <select
                value={currentDocument?.id || ''}
                onChange={e => handleSelectDoc(e.target.value)}
                style={{ padding: '6px 10px', fontSize: '0.8rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}
              >
                <option value="">Switch Document...</option>
                {documents.map((d: DocumentUploadResponse) => (
                  <option key={d.id} value={d.id}>{d.filename}</option>
                ))}
              </select>
            )}
            {!currentDocument && (
              <button className="btn btn--secondary btn--sm" onClick={() => handleQuickDemo('employment-agreement')}>
                Load Demo Agreement
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Search Input Bar */}
      <div className="card" style={{ marginBottom: 20 }}>
        <form
          onSubmit={e => {
            e.preventDefault();
            handleAsk();
          }}
          className="chat-input-row"
        >
          <input
            type="text"
            className="chat-input"
            placeholder="Ask anything about your contract (e.g., notice period, penalty fee, dispute venue)..."
            value={question}
            onChange={e => setQuestion(e.target.value)}
            disabled={loading || !currentDocument}
          />
          <button
            type="submit"
            className="btn btn--primary"
            disabled={loading || !currentDocument || !question.trim()}
          >
            {loading ? <RefreshCw size={16} className="spin-animation" /> : <Send size={16} />}
            {loading ? 'Analyzing...' : 'Ask'}
          </button>
        </form>

        {/* Suggested Questions */}
        <div style={{ marginTop: 14 }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-tertiary)', marginBottom: 6, display: 'flex', alignItems: 'center', gap: 4 }}>
            <Sparkles size={12} /> SUGGESTED VERIFICATION QUERIES:
          </div>
          <div className="chat-suggestions">
            {suggestions.map((sug, i) => (
              <button
                key={i}
                className="chat-suggestion-chip"
                onClick={() => handleAsk(sug)}
                disabled={loading || !currentDocument}
              >
                {sug}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Live Grounded Answer Card */}
      {response && (
        <div className="card" style={{ borderLeft: '4px solid var(--color-accent)', marginBottom: 24 }}>
          <div className="card__header">
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Scale size={18} color="var(--color-accent)" />
              <h3 className="card__title">Evidence-Grounded Answer</h3>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              {getStatusBadge(response.status)}
              <div className="confidence-meter">
                <span>{Math.round(response.confidence * 100)}% Confidence</span>
                <div className="confidence-meter__bar">
                  <div
                    className="confidence-meter__fill"
                    style={{
                      width: `${response.confidence * 100}%`,
                      background: response.confidence > 0.8 ? 'var(--color-verified)' : 'var(--color-high)',
                    }}
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Direct Answer */}
          <div style={{ fontSize: '0.95rem', lineHeight: 1.6, color: 'var(--color-text-primary)', marginBottom: 16 }}>
            {response.directAnswer}
          </div>

          {/* Direct Evidence Citations */}
          {response.evidence && response.evidence.length > 0 && (
            <div style={{ marginBottom: 16 }}>
              <h4 style={{ fontSize: '0.8rem', textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--color-text-secondary)', marginBottom: 8 }}>
                Verbatim Source Excerpts ({response.evidence.length})
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {response.evidence.map((ev: Evidence, i: number) => (
                  <div key={i} style={{ padding: 10, background: 'var(--color-bg-elevated)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.72rem', color: 'var(--color-text-tertiary)', marginBottom: 4 }}>
                      <span>Clause: <strong>{ev.clause}</strong></span>
                      <span>Page: <strong>{ev.pageNumber}</strong></span>
                      <span>Confidence: <strong>{Math.round(ev.confidence * 100)}%</strong></span>
                    </div>
                    <div className="evidence-box" style={{ margin: '4px 0' }}>
                      "{ev.excerpt}"
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* What Was NOT Found — Hallucination Guard */}
          {response.whatWasNotFound && (
            <div style={{ padding: 12, background: 'var(--color-not-found-bg)', border: '1px solid #e7e5e4', borderRadius: 'var(--radius-sm)', marginBottom: 16 }}>
              <h4 style={{ fontSize: '0.82rem', color: 'var(--color-not-found)', display: 'flex', alignItems: 'center', gap: 6, marginBottom: 4 }}>
                <HelpCircle size={14} /> Transparency Audit: What Was Not Found in Document
              </h4>
              <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', margin: 0 }}>
                {response.whatWasNotFound}
              </p>
            </div>
          )}

          {/* Suggested Follow-ups */}
          {response.suggestedNextQuestions && response.suggestedNextQuestions.length > 0 && (
            <div style={{ marginTop: 12 }}>
              <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-tertiary)' }}>
                Recommended Follow-Up Questions:
              </span>
              <div className="chat-suggestions" style={{ marginTop: 6 }}>
                {response.suggestedNextQuestions.map((q, idx) => (
                  <button
                    key={idx}
                    className="chat-suggestion-chip"
                    onClick={() => handleAsk(q)}
                  >
                    {q}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Legal Safety Notice */}
          <div style={{ marginTop: 16, paddingTop: 12, borderTop: '1px solid var(--border-color)', fontSize: '0.72rem', color: 'var(--color-text-tertiary)' }}>
            ⚖️ <strong>Legal Notice:</strong> {response.legalSafetyNote} (Model: {response.modelUsed})
          </div>
        </div>
      )}
    </div>
  );
}
