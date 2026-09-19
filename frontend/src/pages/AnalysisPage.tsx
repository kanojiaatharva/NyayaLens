import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FileText,
  HelpCircle,
  Clock,
  DollarSign,
  Users,
  Scale,
  Search,
  BookOpen,
  ShieldAlert,
  RefreshCw,
} from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { Claim, Evidence, DocumentSection } from '../types';
import { plainLanguageDictionary } from '../i18n/translations';

export default function AnalysisPage() {
  const {
    currentDocument,
    setCurrentDocument,
    currentAnalysis,
    setCurrentAnalysis,
    documents,
    plainLanguage,
  } = useApp();

  const navigate = useNavigate();

  // State
  const [selectedClaim, setSelectedClaim] = useState<Claim | null>(null);
  const [selectedSectionId, setSelectedSectionId] = useState<string | null>(null);
  const [clauseSearch, setClauseSearch] = useState('');
  const [activeTab, setActiveTab] = useState<'risks' | 'obligations' | 'deadlines' | 'financial' | 'terms'>('risks');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Auto-select first risk if available
  useEffect(() => {
    if (currentAnalysis && currentAnalysis.risks && currentAnalysis.risks.length > 0 && !selectedClaim) {
      setSelectedClaim(currentAnalysis.risks[0]);
    }
  }, [currentAnalysis, selectedClaim]);

  // Load document if not loaded but documents exist
  useEffect(() => {
    if (!currentDocument && documents.length > 0) {
      const first = documents[0];
      setLoading(true);
      api.getDocument(first.id)
        .then(doc => {
          setCurrentDocument(doc);
          return api.analyzeDocument(doc.id);
        })
        .then(analysis => setCurrentAnalysis(analysis))
        .catch(err => setError(err instanceof Error ? err.message : 'Failed to load analysis.'))
        .finally(() => setLoading(false));
    }
  }, [currentDocument, documents, setCurrentDocument, setCurrentAnalysis]);

  const handleSelectClaim = (claim: Claim) => {
    setSelectedClaim(claim);
    if (claim.evidenceList && claim.evidenceList.length > 0) {
      const ev = claim.evidenceList[0];
      // Try to find corresponding section in document
      if (currentDocument) {
        const found = currentDocument.sections.find(
          s => s.clauseNumber === ev.clause || s.content.includes(ev.excerpt.slice(0, 30))
        );
        if (found) {
          setSelectedSectionId(found.id);
          const el = document.getElementById(`clause-${found.id}`);
          if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      }
    }
  };

  const simplifyText = (text: string): string => {
    if (!plainLanguage) return text;
    let modified = text;
    Object.entries(plainLanguageDictionary).forEach(([term, definition]) => {
      const regex = new RegExp(`\\b${term}\\b`, 'gi');
      if (regex.test(modified)) {
        modified = modified.replace(regex, `**${term}** [💡 ${definition}]`);
      }
    });
    return modified;
  };

  if (loading) {
    return (
      <div className="loading-spinner" style={{ minHeight: 400 }}>
        <RefreshCw size={24} className="spin-animation" style={{ marginRight: 8 }} />
        Running evidence-grounded legal analysis pipeline...
      </div>
    );
  }

  if (!currentDocument || !currentAnalysis) {
    return (
      <div style={{ textAlign: 'center', padding: '60px 20px', maxWidth: 600, margin: '0 auto' }}>
        <FileText size={48} color="var(--color-accent)" style={{ marginBottom: 16 }} />
        <h2>No Document Selected for Analysis</h2>
        <p style={{ color: 'var(--color-text-secondary)', marginBottom: 24 }}>
          Upload a contract or choose one of our demonstration scenarios to inspect verified clauses, risk triggers, and evidence grounding.
        </p>
        <button className="btn btn--primary" onClick={() => navigate('/upload')}>
          Go to Upload & Ingest
        </button>
      </div>
    );
  }

  const filteredSections = currentDocument.sections.filter(s =>
    clauseSearch === '' ||
    s.title.toLowerCase().includes(clauseSearch.toLowerCase()) ||
    s.content.toLowerCase().includes(clauseSearch.toLowerCase()) ||
    s.clauseNumber.toLowerCase().includes(clauseSearch.toLowerCase())
  );

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'VERIFIED':
        return <span className="badge badge--verified">✓ VERIFIED</span>;
      case 'INFERRED':
        return <span className="badge badge--inferred">ℹ INFERRED</span>;
      case 'NOT_FOUND':
        return <span className="badge badge--not-found">✕ NOT FOUND</span>;
      case 'NEEDS_REVIEW':
      default:
        return <span className="badge badge--needs-review">⚠ NEEDS REVIEW</span>;
    }
  };

  const getSeverityBadge = (sev: string) => {
    switch (sev) {
      case 'CRITICAL':
        return <span className="badge badge--critical">CRITICAL RISK</span>;
      case 'HIGH':
        return <span className="badge badge--high">HIGH RISK</span>;
      case 'MEDIUM':
        return <span className="badge badge--medium">MEDIUM</span>;
      case 'LOW':
      default:
        return <span className="badge badge--low">LOW</span>;
    }
  };

  return (
    <div>
      {/* Top Banner: Document Metadata & Mode Indicator */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: 12,
          marginBottom: 16,
          padding: '12px 16px',
          background: 'var(--color-bg-card)',
          border: '1px solid var(--border-color)',
          borderRadius: 'var(--radius-md)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <FileText size={20} color="var(--color-accent)" />
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <h2 style={{ fontSize: '1.05rem', margin: 0 }}>{currentDocument.filename}</h2>
              <span className="badge badge--inferred">{currentAnalysis.documentType}</span>
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--color-text-secondary)', marginTop: 2 }}>
              {currentDocument.pageCount} Pages • {currentDocument.sections.length} Clauses • Model: {currentAnalysis.modelUsed}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <button className="btn btn--secondary btn--sm" onClick={() => navigate('/ask')}>
            Ask Questions
          </button>
          <button className="btn btn--secondary btn--sm" onClick={() => navigate('/actionpath')}>
            ActionPath
          </button>
          <button className="btn btn--secondary btn--sm" onClick={() => navigate('/compare')}>
            Compare
          </button>
        </div>
      </div>

      {error && (
        <div className="error-message" style={{ marginBottom: 16 }}>
          {error}
        </div>
      )}

      {/* Flagship 3-Column Analysis Workspace */}
      <div className="analysis-workspace">
        {/* ================= COLUMN 1: Document & Clause Navigator ================= */}
        <aside className="analysis-workspace__left" aria-label="Document Sections">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
            <h3 style={{ fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--color-text-secondary)' }}>
              Source Clauses ({currentDocument.sections.length})
            </h3>
          </div>

          <div style={{ position: 'relative', marginBottom: 12 }}>
            <input
              type="text"
              placeholder="Filter clauses..."
              value={clauseSearch}
              onChange={e => setClauseSearch(e.target.value)}
              style={{
                width: '100%',
                padding: '6px 26px 6px 8px',
                fontSize: '0.78rem',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--radius-sm)',
              }}
            />
            <Search size={14} style={{ position: 'absolute', right: 8, top: 8, color: 'var(--color-text-tertiary)' }} />
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
            {filteredSections.map((sec: DocumentSection) => {
              const isSelected = selectedSectionId === sec.id;
              return (
                <div
                  id={`clause-${sec.id}`}
                  key={sec.id}
                  className={`doc-pages__item ${isSelected ? 'doc-pages__item--active' : ''}`}
                  onClick={() => setSelectedSectionId(sec.id)}
                  role="button"
                  tabIndex={0}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 2 }}>
                    <strong style={{ fontSize: '0.75rem', color: isSelected ? 'var(--color-accent)' : 'inherit' }}>
                      {sec.clauseNumber ? `Clause ${sec.clauseNumber}` : `Section`}
                    </strong>
                    <span style={{ fontSize: '0.68rem', color: 'var(--color-text-tertiary)' }}>P.{sec.pageNumber}</span>
                  </div>
                  <div style={{ fontWeight: 600, fontSize: '0.8rem', marginBottom: 4 }}>
                    {sec.title || 'Untitled Section'}
                  </div>
                  <div style={{ fontSize: '0.72rem', color: 'var(--color-text-secondary)', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                    {sec.content}
                  </div>
                </div>
              );
            })}
          </div>
        </aside>

        {/* ================= COLUMN 2: Analysis Stream ================= */}
        <section className="analysis-workspace__center" aria-label="Analysis Stream">
          {/* Plain-Language Summary Box */}
          <div className="card" style={{ borderLeft: '4px solid var(--color-accent)' }}>
            <div className="card__header">
              <h3 className="card__title" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <Scale size={18} color="var(--color-accent)" /> Plain-Language Executive Summary
              </h3>
              {plainLanguage && (
                <span className="badge badge--low">Simplified Mode On</span>
              )}
            </div>
            <p style={{ fontSize: '0.88rem', lineHeight: 1.6, color: 'var(--color-text-primary)' }}>
              {simplifyText(currentAnalysis.plainSummary)}
            </p>

            {/* Core Contract Attributes */}
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
                gap: 12,
                marginTop: 14,
                paddingTop: 12,
                borderTop: '1px solid var(--border-color)',
                fontSize: '0.78rem',
              }}
            >
              <div>
                <span style={{ color: 'var(--color-text-tertiary)', display: 'block' }}>Parties</span>
                <strong>{currentAnalysis.parties.map(p => `${p.name} (${p.role})`).join(' vs ') || 'Not specified'}</strong>
              </div>
              <div>
                <span style={{ color: 'var(--color-text-tertiary)', display: 'block' }}>Effective Date</span>
                <strong>{currentAnalysis.effectiveDate || 'Not explicitly stated'}</strong>
              </div>
              <div>
                <span style={{ color: 'var(--color-text-tertiary)', display: 'block' }}>Governing Law</span>
                <strong>{currentAnalysis.governingLaw || 'Not explicitly stated'}</strong>
              </div>
              <div>
                <span style={{ color: 'var(--color-text-tertiary)', display: 'block' }}>Jurisdiction</span>
                <strong>{currentAnalysis.jurisdiction || 'Not explicitly stated'}</strong>
              </div>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div style={{ display: 'flex', gap: 8, borderBottom: '1px solid var(--border-color)', paddingBottom: 4 }}>
            <button
              className={`btn btn--sm ${activeTab === 'risks' ? 'btn--primary' : 'btn--secondary'}`}
              onClick={() => setActiveTab('risks')}
            >
              <ShieldAlert size={14} /> Risk Scanner ({currentAnalysis.risks?.length || 0})
            </button>
            <button
              className={`btn btn--sm ${activeTab === 'obligations' ? 'btn--primary' : 'btn--secondary'}`}
              onClick={() => setActiveTab('obligations')}
            >
              <Users size={14} /> Obligations ({currentAnalysis.obligations?.length || 0})
            </button>
            <button
              className={`btn btn--sm ${activeTab === 'deadlines' ? 'btn--primary' : 'btn--secondary'}`}
              onClick={() => setActiveTab('deadlines')}
            >
              <Clock size={14} /> Deadlines ({currentAnalysis.deadlines?.length || 0})
            </button>
            <button
              className={`btn btn--sm ${activeTab === 'financial' ? 'btn--primary' : 'btn--secondary'}`}
              onClick={() => setActiveTab('financial')}
            >
              <DollarSign size={14} /> Financial ({currentAnalysis.financialTerms?.length || 0})
            </button>
            <button
              className={`btn btn--sm ${activeTab === 'terms' ? 'btn--primary' : 'btn--secondary'}`}
              onClick={() => setActiveTab('terms')}
            >
              <BookOpen size={14} /> Termination & Renewal
            </button>
          </div>

          {/* TAB 1: Risk Scanner */}
          {activeTab === 'risks' && (
            <div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
                <span style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)' }}>
                  Click any risk claim below to inspect verbatim evidence & verification status in the right pane.
                </span>
              </div>

              {currentAnalysis.risks?.map((risk: Claim) => {
                const isSelected = selectedClaim?.id === risk.id;
                return (
                  <div
                    key={risk.id}
                    className={`risk-item ${isSelected ? 'risk-item--selected' : ''}`}
                    onClick={() => handleSelectClaim(risk)}
                    role="button"
                    tabIndex={0}
                  >
                    <div className="risk-item__header">
                      {getSeverityBadge(risk.severity)}
                      {getStatusBadge(risk.status)}
                      <span style={{ fontSize: '0.72rem', color: 'var(--color-text-tertiary)', marginLeft: 'auto' }}>
                        {risk.category}
                      </span>
                    </div>

                    <div style={{ fontWeight: 600, fontSize: '0.88rem', margin: '4px 0', color: 'var(--color-text-primary)' }}>
                      {simplifyText(risk.claimText)}
                    </div>

                    <div className="risk-item__text">
                      {simplifyText(risk.explanation)}
                    </div>

                    {risk.lawyerQuestion && (
                      <div className="risk-item__question">
                        <strong>Ask Qualified Lawyer:</strong> {risk.lawyerQuestion}
                      </div>
                    )}
                  </div>
                );
              })}

              {/* Missing or Ambiguous clauses section */}
              {currentAnalysis.missingOrAmbiguousInformation && currentAnalysis.missingOrAmbiguousInformation.length > 0 && (
                <div className="card" style={{ marginTop: 16, background: '#fffbeb', borderColor: '#fde68a' }}>
                  <h4 style={{ fontSize: '0.9rem', color: '#92400e', marginBottom: 8, display: 'flex', alignItems: 'center', gap: 6 }}>
                    <HelpCircle size={16} /> Missing or Ambiguous Terms Detected
                  </h4>
                  <ul style={{ paddingLeft: 20, fontSize: '0.8rem', color: '#78350f' }}>
                    {currentAnalysis.missingOrAmbiguousInformation.map((item, idx) => (
                      <li key={idx} style={{ marginBottom: 4 }}>{item}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          )}

          {/* TAB 2: Obligations */}
          {activeTab === 'obligations' && (
            <div>
              {currentAnalysis.obligations?.map((ob, idx) => (
                <div key={idx} className="card" style={{ marginBottom: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span className="badge badge--inferred">Party: {ob.party}</span>
                    <span style={{ fontSize: '0.72rem', color: 'var(--color-text-tertiary)' }}>Clause {ob.clause} (P.{ob.page})</span>
                  </div>
                  <div style={{ fontSize: '0.85rem', fontWeight: 500, margin: '6px 0' }}>
                    {simplifyText(ob.description)}
                  </div>
                  {ob.excerpt && <div className="evidence-box">"{ob.excerpt}"</div>}
                </div>
              ))}
            </div>
          )}

          {/* TAB 3: Deadlines */}
          {activeTab === 'deadlines' && (
            <div>
              {currentAnalysis.deadlines?.map((dl, idx) => (
                <div key={idx} className="card" style={{ marginBottom: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span className="badge badge--high">⏱ {dl.dateOrDuration}</span>
                    <span style={{ fontSize: '0.72rem', color: 'var(--color-text-tertiary)' }}>Clause {dl.clause} (P.{dl.page})</span>
                  </div>
                  <div style={{ fontSize: '0.85rem', fontWeight: 500, margin: '6px 0' }}>
                    {simplifyText(dl.description)}
                  </div>
                  {dl.excerpt && <div className="evidence-box">"{dl.excerpt}"</div>}
                </div>
              ))}
            </div>
          )}

          {/* TAB 4: Financial Terms */}
          {activeTab === 'financial' && (
            <div>
              {currentAnalysis.financialTerms?.map((ft, idx) => (
                <div key={idx} className="card" style={{ marginBottom: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span className="badge badge--verified">{ft.type}</span>
                    <strong style={{ fontSize: '0.85rem', color: 'var(--color-accent)' }}>{ft.amount}</strong>
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--color-text-secondary)', marginBottom: 4 }}>
                    Clause {ft.clause} (Page {ft.page})
                  </div>
                  {ft.excerpt && <div className="evidence-box">"{ft.excerpt}"</div>}
                </div>
              ))}
            </div>
          )}

          {/* TAB 5: Termination & Renewal */}
          {activeTab === 'terms' && (
            <div>
              <h4 style={{ fontSize: '0.95rem', marginBottom: 8 }}>Termination Conditions</h4>
              {currentAnalysis.terminationConditions?.map((tc, idx) => (
                <div key={idx} className="card" style={{ marginBottom: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span className="badge badge--critical">Notice: {tc.noticePeriod}</span>
                    <span style={{ fontSize: '0.72rem', color: 'var(--color-text-tertiary)' }}>Clause {tc.clause} (P.{tc.page})</span>
                  </div>
                  <p style={{ fontSize: '0.82rem', margin: '4px 0' }}>{tc.description}</p>
                  {tc.excerpt && <div className="evidence-box">"{tc.excerpt}"</div>}
                </div>
              ))}

              <h4 style={{ fontSize: '0.95rem', margin: '16px 0 8px' }}>Dispute Resolution & Jurisdiction</h4>
              {currentAnalysis.disputeResolution && (
                <div className="card">
                  <div style={{ display: 'flex', gap: 8, marginBottom: 8 }}>
                    <span className="badge badge--inferred">Method: {currentAnalysis.disputeResolution.method}</span>
                    <span className="badge badge--inferred">Venue: {currentAnalysis.disputeResolution.venue}</span>
                  </div>
                  {currentAnalysis.disputeResolution.excerpt && (
                    <div className="evidence-box">"{currentAnalysis.disputeResolution.excerpt}"</div>
                  )}
                </div>
              )}
            </div>
          )}
        </section>

        {/* ================= COLUMN 3: Evidence Inspector ================= */}
        <aside className="analysis-workspace__right" aria-label="Evidence Inspector">
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 12, paddingBottom: 8, borderBottom: '1px solid var(--border-color)' }}>
            <Scale size={18} color="var(--color-accent)" />
            <h3 style={{ fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--color-text-secondary)', margin: 0 }}>
              Evidence Inspector
            </h3>
          </div>

          {!selectedClaim ? (
            <p style={{ fontSize: '0.8rem', color: 'var(--color-text-tertiary)', textAlign: 'center', padding: '30px 0' }}>
              Select any claim or risk from the middle stream to audit exact source excerpts, clause numbers, and verification metrics.
            </p>
          ) : (
            <div>
              {/* Claim Title */}
              <div style={{ marginBottom: 12 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 6 }}>
                  {getStatusBadge(selectedClaim.status)}
                  {getSeverityBadge(selectedClaim.severity)}
                </div>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>
                  {selectedClaim.claimText}
                </h4>
              </div>

              {/* Confidence Meter */}
              <div style={{ margin: '12px 0', padding: '8px 12px', background: 'var(--color-bg-elevated)', borderRadius: 'var(--radius-sm)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', marginBottom: 4 }}>
                  <span style={{ color: 'var(--color-text-secondary)' }}>Verification Confidence</span>
                  <strong>{Math.round(selectedClaim.confidence * 100)}%</strong>
                </div>
                <div className="confidence-meter__bar" style={{ maxWidth: '100%', height: 6 }}>
                  <div
                    className="confidence-meter__fill"
                    style={{
                      width: `${selectedClaim.confidence * 100}%`,
                      background: selectedClaim.confidence > 0.8 ? 'var(--color-verified)' : 'var(--color-high)',
                    }}
                  />
                </div>
              </div>

              {/* Exact Evidence Excerpts */}
              <div style={{ marginTop: 14 }}>
                <h5 style={{ fontSize: '0.8rem', textTransform: 'uppercase', letterSpacing: '0.04em', color: 'var(--color-text-secondary)', marginBottom: 6 }}>
                  Source Grounding
                </h5>

                {selectedClaim.evidenceList && selectedClaim.evidenceList.length > 0 ? (
                  selectedClaim.evidenceList.map((ev: Evidence, i: number) => (
                    <div
                      key={i}
                      style={{
                        padding: 10,
                        border: '1px solid var(--border-color)',
                        borderRadius: 'var(--radius-sm)',
                        background: '#ffffff',
                        marginBottom: 8,
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.72rem', color: 'var(--color-text-tertiary)', marginBottom: 4 }}>
                        <span>Clause: <strong>{ev.clause || 'N/A'}</strong></span>
                        <span>Page: <strong>{ev.pageNumber}</strong></span>
                      </div>
                      <div className="evidence-box" style={{ margin: '4px 0', fontSize: '0.78rem' }}>
                        "{ev.excerpt}"
                      </div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--color-text-tertiary)', marginTop: 4 }}>
                        Source: {ev.documentName || currentDocument.filename}
                      </div>
                    </div>
                  ))
                ) : (
                  <div style={{ padding: 12, background: 'var(--color-not-found-bg)', borderRadius: 'var(--radius-sm)', fontSize: '0.78rem', color: 'var(--color-not-found)' }}>
                    No direct textual evidence found. Status is inferred or requires review.
                  </div>
                )}
              </div>

              {/* Legal Boundary Notice */}
              <div
                style={{
                  marginTop: 16,
                  padding: 10,
                  background: 'var(--color-accent-bg)',
                  border: '1px solid var(--color-accent-border)',
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.72rem',
                  color: 'var(--color-accent)',
                  lineHeight: 1.4,
                }}
              >
                <strong>Audit Note:</strong> All findings are strictly bounded by document text. Consult qualified counsel before taking action.
              </div>
            </div>
          )}
        </aside>
      </div>
    </div>
  );
}
