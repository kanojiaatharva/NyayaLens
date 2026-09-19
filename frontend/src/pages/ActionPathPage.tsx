import { useState, useEffect } from 'react';
import {
  Route as RouteIcon,
  CheckSquare,
  MessageSquare,
  ExternalLink,
  Printer,
  FileText,
  AlertTriangle,
  RefreshCw,
  Scale,
  ShieldCheck,
} from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { ActionPathResult, ActionStep, LawyerQuestion, OfficialResourceItem, DocumentUploadResponse } from '../types';

export default function ActionPathPage() {
  const { currentDocument, setCurrentDocument, documents, setDocuments, t } = useApp();

  const [actionPath, setActionPath] = useState<ActionPathResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (currentDocument && !actionPath) {
      loadActionPath(currentDocument.id);
    }
  }, [currentDocument]);

  const loadActionPath = async (docId: string) => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getActionPath(docId);
      setActionPath(res);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to generate ActionPath.');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectDoc = async (docId: string) => {
    try {
      const doc = await api.getDocument(docId);
      setCurrentDocument(doc);
      loadActionPath(docId);
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
      loadActionPath(fullDoc.id);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load demo.');
    } finally {
      setLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const getPriorityBadge = (p: string) => {
    switch (p) {
      case 'HIGH':
        return <span className="badge badge--critical">HIGH PRIORITY</span>;
      case 'MEDIUM':
        return <span className="badge badge--medium">MEDIUM PRIORITY</span>;
      default:
        return <span className="badge badge--low">LOW PRIORITY</span>;
    }
  };

  return (
    <div style={{ maxWidth: 960, margin: '0 auto' }}>
      <div className="section-header" style={{ justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <RouteIcon className="section-header__icon" size={24} />
          <div>
            <h2>{t.nav.actionpath}</h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
              Structured preparation guide: evidence checklist, strategic questions for legal counsel, and official statutory remedies.
            </p>
          </div>
        </div>

        {actionPath && (
          <button className="btn btn--secondary btn--sm" onClick={handlePrint} title="Print or save as PDF">
            <Printer size={14} /> {t.actions.printActionPath}
          </button>
        )}
      </div>

      {error && (
        <div className="error-message" style={{ marginBottom: 16 }}>
          <AlertTriangle size={16} style={{ display: 'inline', marginRight: 6, verticalAlign: 'text-bottom' }} />
          {error}
        </div>
      )}

      {/* Active Document Bar */}
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

      {loading && (
        <div className="loading-spinner" style={{ padding: 40 }}>
          <RefreshCw size={20} className="spin-animation" style={{ marginRight: 8 }} />
          Synthesizing procedural preparation path and lawyer checklist...
        </div>
      )}

      {actionPath && (
        <div>
          {/* Situation Overview */}
          <div className="card" style={{ marginBottom: 20, borderLeft: '4px solid var(--color-accent)' }}>
            <h3 style={{ fontSize: '1rem', marginBottom: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
              <Scale size={18} color="var(--color-accent)" /> Situation Overview & Strategic Posture
            </h3>
            <p style={{ fontSize: '0.88rem', lineHeight: 1.6, color: 'var(--color-text-primary)', margin: 0 }}>
              {actionPath.situationOverview}
            </p>
          </div>

          {/* Action Step Checklist */}
          <div className="card" style={{ marginBottom: 24 }}>
            <div className="card__header">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <CheckSquare size={18} color="var(--color-accent)" />
                <h3 className="card__title">Step-by-Step Procedural Preparation Checklist</h3>
              </div>
              <span style={{ fontSize: '0.75rem', color: 'var(--color-text-tertiary)' }}>
                {actionPath.checklist.length} actionable steps
              </span>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column' }}>
              {actionPath.checklist.map((step: ActionStep) => (
                <div key={step.stepNumber} className="actionpath-step">
                  <div className="actionpath-step__number">{step.stepNumber}</div>
                  <div className="actionpath-step__body" style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 4 }}>
                      <h4>{step.title}</h4>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                        <span className="badge badge--inferred">{step.category}</span>
                        {getPriorityBadge(step.priority)}
                      </div>
                    </div>
                    <p>{step.action}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Questions To Ask Your Lawyer */}
          <div className="card" style={{ marginBottom: 24 }}>
            <div className="card__header">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <MessageSquare size={18} color="var(--color-accent)" />
                <h3 className="card__title">Specific Questions to Ask Qualified Legal Counsel</h3>
              </div>
              <span className="badge badge--verified">Printable Consultation Brief</span>
            </div>
            <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', marginBottom: 14 }}>
              Take these exact, clause-referenced questions to your initial consultation to maximize legal efficacy and minimize billable hours.
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {actionPath.questionsToAskLawyer.map((q: LawyerQuestion, i: number) => (
                <div
                  key={i}
                  style={{
                    padding: 12,
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-sm)',
                    background: 'var(--color-bg-elevated)',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 4 }}>
                    <strong style={{ fontSize: '0.88rem', color: 'var(--color-text-primary)' }}>
                      Q{i + 1}: {q.question}
                    </strong>
                    {q.relevantClause && (
                      <span className="badge badge--needs-review" style={{ marginLeft: 8 }}>
                        Clause {q.relevantClause}
                      </span>
                    )}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginTop: 4 }}>
                    <strong>Why this matters:</strong> {q.context}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Recommended Statutory & Institutional Resources */}
          <div className="card" style={{ marginBottom: 24 }}>
            <div className="card__header">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <ShieldCheck size={18} color="var(--color-accent)" />
                <h3 className="card__title">Official Institutional & Legal Aid Channels</h3>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: 12 }}>
              {actionPath.recommendedOfficialResources.map((res: OfficialResourceItem, i: number) => (
                <div
                  key={i}
                  style={{
                    padding: 12,
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-sm)',
                    background: '#ffffff',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                  }}
                >
                  <div>
                    <strong style={{ fontSize: '0.88rem' }}>{res.name}</strong>
                    <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)', margin: '4px 0 10px' }}>
                      {res.description}
                    </p>
                  </div>
                  <a
                    href={res.officialUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="resource-item__link"
                    style={{ marginTop: 'auto' }}
                  >
                    Official Portal <ExternalLink size={12} />
                  </a>
                </div>
              ))}
            </div>
          </div>

          {/* Legal Boundaries Footer */}
          <div
            style={{
              padding: 14,
              background: 'var(--color-accent-bg)',
              border: '1px solid var(--color-accent-border)',
              borderRadius: 'var(--radius-md)',
              fontSize: '0.75rem',
              color: 'var(--color-accent)',
              textAlign: 'center',
            }}
          >
            ⚖️ <strong>Compliance Notice:</strong> This ActionPath is generated as a structured preparation brief for professional consultation. NyayaLens does not represent, file, or advocate in judicial forums.
          </div>
        </div>
      )}
    </div>
  );
}
