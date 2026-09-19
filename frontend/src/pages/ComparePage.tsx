import { useState, useEffect } from 'react';
import { GitCompare, ArrowRight, AlertTriangle, RefreshCw, Scale } from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { ComparisonResult, ClauseDelta, DocumentUploadResponse } from '../types';

export default function ComparePage() {
  const { documents, setDocuments, t } = useApp();

  const [docAId, setDocAId] = useState<string>('');
  const [docBId, setDocBId] = useState<string>('');
  const [comparison, setComparison] = useState<ComparisonResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadingDemo, setLoadingDemo] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (documents.length >= 2) {
      if (!docAId) setDocAId(documents[0].id);
      if (!docBId) setDocBId(documents[1].id);
    } else if (documents.length === 1 && !docAId) {
      setDocAId(documents[0].id);
    }
  }, [documents, docAId, docBId]);

  const handleCompare = async () => {
    if (!docAId || !docBId) {
      setError('Please select two different documents to compare.');
      return;
    }
    if (docAId === docBId) {
      setError('Please select two distinct documents.');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const res = await api.compareDocuments(docAId, docBId);
      setComparison(res);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Comparison failed.');
    } finally {
      setLoading(false);
    }
  };

  const handleLoadDemoPair = async (pairType: 'employment' | 'lease' | 'nda') => {
    setLoadingDemo(true);
    setError(null);
    try {
      let scenarioA = '';
      let scenarioB = '';

      if (pairType === 'employment') {
        scenarioA = 'employment-agreement';
        scenarioB = 'termination-notice';
      } else if (pairType === 'lease') {
        scenarioA = 'rental-agreement';
        scenarioB = 'rent-increase-notice';
      } else {
        scenarioA = 'mutual-nda';
        scenarioB = 'unilateral-vendor-nda';
      }

      const docA = await api.loadDemoDocument(scenarioA);
      const docB = await api.loadDemoDocument(scenarioB);

      setDocuments(prev => {
        const ids = new Set(prev.map(d => d.id));
        const updated = [...prev];
        if (!ids.has(docA.id)) updated.push(docA);
        if (!ids.has(docB.id)) updated.push(docB);
        return updated;
      });

      setDocAId(docA.id);
      setDocBId(docB.id);

      // Trigger compare immediately
      const res = await api.compareDocuments(docA.id, docB.id);
      setComparison(res);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load demo pair.');
    } finally {
      setLoadingDemo(false);
    }
  };

  const getSeverityBadge = (sev: string) => {
    switch (sev) {
      case 'CRITICAL':
        return <span className="badge badge--critical">CRITICAL IMPACT</span>;
      case 'HIGH':
        return <span className="badge badge--high">HIGH IMPACT</span>;
      case 'MEDIUM':
        return <span className="badge badge--medium">MEDIUM</span>;
      default:
        return <span className="badge badge--low">LOW</span>;
    }
  };

  const getChangeBadge = (change: string) => {
    switch (change) {
      case 'ADDED':
        return <span className="badge badge--high">+ ADDED</span>;
      case 'REMOVED':
        return <span className="badge badge--critical">- REMOVED</span>;
      case 'MODIFIED':
        return <span className="badge badge--needs-review">~ MODIFIED</span>;
      default:
        return <span className="badge badge--low">= UNCHANGED</span>;
    }
  };

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto' }}>
      <div className="section-header">
        <GitCompare className="section-header__icon" size={24} />
        <div>
          <h2>{t.nav.compare}</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
            Compare contracts, notices, or updated agreements. Pinpoint added obligations, eliminated rights, and conflicting terms.
          </p>
        </div>
      </div>

      {error && (
        <div className="error-message" style={{ marginBottom: 16 }}>
          <AlertTriangle size={16} style={{ display: 'inline', marginRight: 6, verticalAlign: 'text-bottom' }} />
          {error}
        </div>
      )}

      {/* Quick Demo Pair Presets */}
      <div className="card" style={{ marginBottom: 20 }}>
        <h3 style={{ fontSize: '0.95rem', marginBottom: 10 }}>Load Pre-configured Demo Pairs:</h3>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
          <button
            className="btn btn--secondary btn--sm"
            onClick={() => handleLoadDemoPair('employment')}
            disabled={loadingDemo}
          >
            ⚖️ Employment Agreement vs Termination Notice
          </button>
          <button
            className="btn btn--secondary btn--sm"
            onClick={() => handleLoadDemoPair('lease')}
            disabled={loadingDemo}
          >
            🏠 Residential Lease vs 25% Increase Notice
          </button>
          <button
            className="btn btn--secondary btn--sm"
            onClick={() => handleLoadDemoPair('nda')}
            disabled={loadingDemo}
          >
            🔒 Mutual NDA vs Aggressive Vendor NDA
          </button>
        </div>
        {loadingDemo && <div className="loading-spinner" style={{ padding: 12 }}>Loading pair and executing semantic diff...</div>}
      </div>

      {/* Document Selectors */}
      <div className="card" style={{ marginBottom: 24 }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr auto 1fr', gap: 16, alignItems: 'center' }}>
          <div>
            <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: 6 }}>
              Document A (Base / Baseline Contract)
            </label>
            <select
              value={docAId}
              onChange={e => setDocAId(e.target.value)}
              style={{ width: '100%', padding: '8px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', fontSize: '0.85rem' }}
            >
              <option value="">Select Document A...</option>
              {documents.map((d: DocumentUploadResponse) => (
                <option key={d.id} value={d.id}>{d.filename}</option>
              ))}
            </select>
          </div>

          <div style={{ textAlign: 'center', paddingTop: 18 }}>
            <ArrowRight size={20} color="var(--color-text-tertiary)" />
          </div>

          <div>
            <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: 6 }}>
              Document B (New Draft / Notice / Counterparty Agreement)
            </label>
            <select
              value={docBId}
              onChange={e => setDocBId(e.target.value)}
              style={{ width: '100%', padding: '8px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', fontSize: '0.85rem' }}
            >
              <option value="">Select Document B...</option>
              {documents.map((d: DocumentUploadResponse) => (
                <option key={d.id} value={d.id}>{d.filename}</option>
              ))}
            </select>
          </div>
        </div>

        <div style={{ marginTop: 16, textAlign: 'right' }}>
          <button
            className="btn btn--primary"
            onClick={handleCompare}
            disabled={loading || !docAId || !docBId}
          >
            {loading ? <RefreshCw size={16} className="spin-animation" /> : <GitCompare size={16} />}
            {loading ? 'Running Semantic Diff...' : 'Execute Document Comparison'}
          </button>
        </div>
      </div>

      {/* Comparison Results */}
      {comparison && (
        <div>
          {/* Executive Diff Summary */}
          <div className="card" style={{ marginBottom: 20, borderLeft: '4px solid var(--color-accent)' }}>
            <div className="card__header">
              <h3 className="card__title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <Scale size={18} color="var(--color-accent)" /> Semantic Delta Summary
              </h3>
              {getSeverityBadge(comparison.overallImpact)}
            </div>
            <p style={{ fontSize: '0.9rem', lineHeight: 1.6, color: 'var(--color-text-primary)' }}>
              {comparison.comparisonSummary}
            </p>
            <div style={{ fontSize: '0.75rem', color: 'var(--color-text-tertiary)', marginTop: 8 }}>
              Comparing <strong>{comparison.documentAName}</strong> against <strong>{comparison.documentBName}</strong> • Powered by {comparison.modelUsed}
            </div>
          </div>

          {/* Deltas Breakdown */}
          <h3 style={{ fontSize: '1rem', marginBottom: 12 }}>
            Clause-by-Clause Impact Analysis ({comparison.deltas.length} Significant Discrepancies)
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            {comparison.deltas.map((delta: ClauseDelta) => (
              <div key={delta.id} className="comparison-delta">
                <div className="comparison-delta__header">
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <strong style={{ fontSize: '0.95rem' }}>{delta.clauseTopic}</strong>
                    {getChangeBadge(delta.changeType)}
                    {getSeverityBadge(delta.severity)}
                  </div>
                </div>

                <div className="comparison-delta__sides">
                  {/* Side A */}
                  <div className="comparison-delta__side comparison-delta__side--a">
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', marginBottom: 4 }}>
                      <strong>{comparison.documentAName}</strong>
                      <span>Clause {delta.docAClause || 'N/A'} (P.{delta.docAPage})</span>
                    </div>
                    <div style={{ fontStyle: 'italic', fontSize: '0.8rem', color: 'var(--color-text-secondary)', lineHeight: 1.4 }}>
                      {delta.docAExcerpt ? `"${delta.docAExcerpt}"` : '(No corresponding clause found in Document A)'}
                    </div>
                  </div>

                  {/* Side B */}
                  <div className="comparison-delta__side comparison-delta__side--b">
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', marginBottom: 4 }}>
                      <strong>{comparison.documentBName}</strong>
                      <span>Clause {delta.docBClause || 'N/A'} (P.{delta.docBPage})</span>
                    </div>
                    <div style={{ fontStyle: 'italic', fontSize: '0.8rem', color: 'var(--color-text-secondary)', lineHeight: 1.4 }}>
                      {delta.docBExcerpt ? `"${delta.docBExcerpt}"` : '(No corresponding clause found in Document B)'}
                    </div>
                  </div>
                </div>

                {/* Impact Assessment & Safe Next Step */}
                <div style={{ marginTop: 12, padding: '10px 12px', background: 'var(--color-accent-bg)', borderRadius: 'var(--radius-sm)', fontSize: '0.8rem' }}>
                  <div style={{ marginBottom: 4, color: 'var(--color-accent)' }}>
                    <strong>Legal & Practical Impact:</strong> {delta.impactAssessment}
                  </div>
                  <div style={{ color: 'var(--color-text-secondary)' }}>
                    <strong>Recommended Safe Action:</strong> {delta.recommendation}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
