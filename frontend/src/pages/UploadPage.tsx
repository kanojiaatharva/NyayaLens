import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Upload, FileText, Trash2, CheckCircle2, AlertTriangle, ShieldCheck, Play } from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { DocumentUploadResponse } from '../types';

export default function UploadPage() {
  const {
    documents,
    setDocuments,
    setCurrentDocument,
    setCurrentAnalysis,
    t,
    privacyMode,
    setPrivacyMode,
  } = useApp();

  const navigate = useNavigate();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [isDragging, setIsDragging] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [analyzingId, setAnalyzingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  useEffect(() => {
    // Refresh document list on mount
    api.listDocuments()
      .then(docs => setDocuments(docs))
      .catch(err => console.error('Failed to fetch documents:', err));
  }, [setDocuments]);

  const handleFileUpload = async (file: File) => {
    setError(null);
    setSuccessMsg(null);
    setUploading(true);

    try {
      const doc = await api.uploadDocument(file, privacyMode);
      setDocuments(prev => [doc, ...prev]);
      setSuccessMsg(`Successfully uploaded and parsed "${file.name}" (${doc.pageCount} pages, ${doc.sectionCount} clauses).`);
      
      // Load full document details into context
      const fullDoc = await api.getDocument(doc.id);
      setCurrentDocument(fullDoc);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to upload document.');
    } finally {
      setUploading(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      handleFileUpload(e.dataTransfer.files[0]);
    }
  };

  const handleDemoLoad = async (scenario: string) => {
    setError(null);
    setSuccessMsg(null);
    setUploading(true);

    try {
      const doc = await api.loadDemoDocument(scenario, privacyMode);
      setDocuments(prev => [doc, ...prev]);
      const fullDoc = await api.getDocument(doc.id);
      setCurrentDocument(fullDoc);
      setSuccessMsg(`Loaded scenario document "${doc.filename}".`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load demo scenario.');
    } finally {
      setUploading(false);
    }
  };

  const handleAnalyze = async (docId: string) => {
    setAnalyzingId(docId);
    setError(null);
    try {
      const fullDoc = await api.getDocument(docId);
      setCurrentDocument(fullDoc);
      const analysis = await api.analyzeDocument(docId);
      setCurrentAnalysis(analysis);
      navigate('/analysis');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Analysis failed.');
    } finally {
      setAnalyzingId(null);
    }
  };

  const handleDelete = async (docId: string) => {
    try {
      await api.deleteDocument(docId);
      setDocuments(prev => prev.filter(d => d.id !== docId));
      setSuccessMsg('Document removed from active session.');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to delete document.');
    }
  };

  const handleWipeAll = async () => {
    if (window.confirm('Wipe all documents from session memory? This will purge in-memory caches and text embeddings.')) {
      try {
        await api.deleteAllSessionData();
        setDocuments([]);
        setCurrentDocument(null);
        setCurrentAnalysis(null);
        setSuccessMsg('Session memory completely wiped.');
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : 'Failed to wipe session.');
      }
    }
  };

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto' }}>
      <div className="section-header">
        <Upload className="section-header__icon" size={24} />
        <div>
          <h2>{t.nav.upload}</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
            Upload legal agreements, contracts, notices, or policies. Documents are parsed securely with client-side & in-memory privacy guards.
          </p>
        </div>
      </div>

      {error && (
        <div className="error-message" style={{ marginBottom: 16 }}>
          <AlertTriangle size={16} style={{ display: 'inline', marginRight: 6, verticalAlign: 'text-bottom' }} />
          {error}
        </div>
      )}

      {successMsg && (
        <div style={{ padding: 12, background: 'var(--color-low-bg)', border: '1px solid var(--color-low-border)', borderRadius: 'var(--radius-md)', color: 'var(--color-low)', fontSize: '0.85rem', marginBottom: 16 }}>
          <CheckCircle2 size={16} style={{ display: 'inline', marginRight: 6, verticalAlign: 'text-bottom' }} />
          {successMsg}
        </div>
      )}

      {/* Privacy Control Card */}
      <div className="card" style={{ marginBottom: 20, background: 'var(--color-accent-bg)', borderColor: 'var(--color-accent-border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <ShieldCheck size={22} color="var(--color-accent)" />
            <div>
              <strong style={{ fontSize: '0.9rem', color: 'var(--color-accent)' }}>Privacy-First Ingestion Mode</strong>
              <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)', margin: 0 }}>
                Automatically detects and redacts Aadhaar (12-digit), PAN (10-char), Phone Numbers, and Email Addresses prior to any GenAI reasoning.
              </p>
            </div>
          </div>
          <label className="toggle">
            <span className={`toggle__track ${privacyMode ? 'toggle__track--active' : ''}`}>
              <span className="toggle__knob" />
            </span>
            <span style={{ fontWeight: 600 }}>{privacyMode ? 'PII Redaction ACTIVE' : 'Raw Text'}</span>
            <input
              type="checkbox"
              checked={privacyMode}
              onChange={() => setPrivacyMode(v => !v)}
              style={{ display: 'none' }}
            />
          </label>
        </div>
      </div>

      {/* Upload Dropzone */}
      <div
        className={`dropzone ${isDragging ? 'dropzone--active' : ''}`}
        onDragOver={(e) => { e.preventDefault(); setIsDragging(true); }}
        onDragLeave={() => setIsDragging(false)}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
        style={{ marginBottom: 24 }}
        role="button"
        tabIndex={0}
        aria-label="Upload document dropzone"
      >
        <Upload className="dropzone__icon" size={40} />
        <div className="dropzone__text">
          <strong>Click to upload</strong> or drag and drop your legal document here
        </div>
        <div className="dropzone__hint">
          Supported formats: PDF (.pdf), Plain Text (.txt), Markdown (.md) up to 15MB
        </div>
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf,.txt,.md"
          onChange={(e) => {
            if (e.target.files && e.target.files.length > 0) {
              handleFileUpload(e.target.files[0]);
            }
          }}
        />
        {uploading && <div className="loading-spinner" style={{ marginTop: 12 }}>Parsing document & extracting clauses...</div>}
      </div>

      {/* Preloaded Demo Scenarios */}
      <div style={{ marginBottom: 32 }}>
        <h3 style={{ fontSize: '1rem', marginBottom: 10 }}>Or Select a Curated Hackathon Demo Document:</h3>
        <div className="demo-grid">
          <div className="demo-card" onClick={() => handleDemoLoad('employment-agreement')}>
            <div className="demo-card__title">📄 TechCorp Employment Agreement</div>
            <div className="demo-card__desc">Includes 60-day notice period, 1-year non-compete, Bengaluru jurisdiction.</div>
          </div>
          <div className="demo-card" onClick={() => handleDemoLoad('termination-notice')}>
            <div className="demo-card__title">⚠️ Immediate Termination Notice</div>
            <div className="demo-card__desc">Contradicts employment agreement: immediate exit, zero severance pay.</div>
          </div>
          <div className="demo-card" onClick={() => handleDemoLoad('rental-agreement')}>
            <div className="demo-card__title">🏠 11-Month Residential Lease</div>
            <div className="demo-card__desc">Standard Bangalore lease with 5% annual escalation ceiling clause.</div>
          </div>
          <div className="demo-card" onClick={() => handleDemoLoad('rent-increase-notice')}>
            <div className="demo-card__title">📈 Landlord 25% Increase Notice</div>
            <div className="demo-card__desc">Breaches contractual 5% cap; demands 15-day compliance under eviction threat.</div>
          </div>
          <div className="demo-card" onClick={() => handleDemoLoad('mutual-nda')}>
            <div className="demo-card__title">🤝 Mutual Non-Disclosure Agreement</div>
            <div className="demo-card__desc">Balanced 2-year term, mutual protection, standard exceptions.</div>
          </div>
          <div className="demo-card" onClick={() => handleDemoLoad('unilateral-vendor-nda')}>
            <div className="demo-card__title">⚖️ Aggressive Vendor NDA</div>
            <div className="demo-card__desc">Perpetual duration, unilateral indemnity, foreign exclusive court jurisdiction.</div>
          </div>
        </div>
      </div>

      {/* Active Session Documents */}
      <div className="card">
        <div className="card__header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <FileText size={18} />
            <h3 className="card__title">Session Documents ({documents.length})</h3>
          </div>
          {documents.length > 0 && (
            <button className="btn btn--danger btn--sm" onClick={handleWipeAll} title="Purge in-memory documents">
              <Trash2 size={14} /> Wipe All Session Data
            </button>
          )}
        </div>

        {documents.length === 0 ? (
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-tertiary)', textAlign: 'center', padding: '24px 0' }}>
            No documents loaded in current session. Upload a file or click any scenario above to begin.
          </p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {documents.map((doc: DocumentUploadResponse) => {
              const piiTotal = doc.piiSummary ? Object.values(doc.piiSummary).reduce((a, b) => a + b, 0) : 0;
              const isAnalyzing = analyzingId === doc.id;

              return (
                <div
                  key={doc.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: 12,
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-sm)',
                    background: 'var(--color-bg-elevated)',
                    flexWrap: 'wrap',
                    gap: 12,
                  }}
                >
                  <div style={{ minWidth: 240 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <FileText size={16} color="var(--color-accent)" />
                      <strong style={{ fontSize: '0.9rem' }}>{doc.filename}</strong>
                      <span className="badge badge--inferred">{doc.fileType.toUpperCase()}</span>
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--color-text-secondary)', marginTop: 4 }}>
                      Pages: {doc.pageCount} | Clauses: {doc.sectionCount} | Size: {(doc.fileSize / 1024).toFixed(1)} KB
                      {doc.piiRedacted && piiTotal > 0 && (
                        <span style={{ marginLeft: 8, color: 'var(--color-verified)', fontWeight: 600 }}>
                          🔒 {piiTotal} PII tokens redacted
                        </span>
                      )}
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <button
                      className="btn btn--primary btn--sm"
                      onClick={() => handleAnalyze(doc.id)}
                      disabled={isAnalyzing}
                    >
                      {isAnalyzing ? (
                        <>Analyzing...</>
                      ) : (
                        <>
                          <Play size={14} /> Analyze Workspace
                        </>
                      )}
                    </button>
                    <button
                      className="btn btn--secondary btn--sm"
                      onClick={() => navigate('/compare')}
                      title="Compare with another document"
                    >
                      Compare
                    </button>
                    <button
                      className="btn btn--secondary btn--sm"
                      onClick={async () => {
                        const full = await api.getDocument(doc.id);
                        setCurrentDocument(full);
                        navigate('/ask');
                      }}
                      title="Ask questions grounded in this document"
                    >
                      Ask
                    </button>
                    <button
                      className="btn btn--danger btn--sm btn--icon"
                      onClick={() => handleDelete(doc.id)}
                      title="Delete document"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
