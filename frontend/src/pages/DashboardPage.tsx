import { useNavigate } from 'react-router-dom';
import { FileText, GitCompare, MessageCircleQuestion, Route, BookOpen, ArrowRight, Shield } from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { useState } from 'react';

export default function DashboardPage() {
  const { t, setDocuments, setCurrentDocument, setCurrentAnalysis } = useApp();
  const navigate = useNavigate();
  const [loading, setLoading] = useState('');

  const loadDemo = async (scenario: string) => {
    setLoading(scenario);
    try {
      const doc = await api.loadDemoDocument(scenario);
      setDocuments(prev => [...prev, doc]);
      const fullDoc = await api.getDocument(doc.id);
      setCurrentDocument(fullDoc);
      const analysis = await api.analyzeDocument(doc.id);
      setCurrentAnalysis(analysis);
      navigate('/analysis');
    } catch (err) {
      console.error('Demo load failed:', err);
    } finally {
      setLoading('');
    }
  };

  return (
    <div>
      {/* Hero */}
      <section style={{ textAlign: 'center', padding: '40px 0 32px' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: 8 }}>{t.appName}</h1>
        <p style={{ fontSize: '1.05rem', color: 'var(--color-text-secondary)', maxWidth: 600, margin: '0 auto 24px' }}>
          {t.tagline}
        </p>
      </section>

      {/* Workflow Cards */}
      <div className="card__grid" style={{ marginBottom: 32 }}>
        {[
          { icon: <FileText size={20} />, title: t.actions.analyze, desc: 'Upload a legal document and extract obligations, risks, deadlines, and parties with evidence grounding.', path: '/upload' },
          { icon: <GitCompare size={20} />, title: t.actions.compareDocs, desc: 'Semantically compare two documents. Identify changed clauses, new obligations, and impact.', path: '/compare' },
          { icon: <MessageCircleQuestion size={20} />, title: t.actions.askQuestion, desc: 'Ask questions grounded in your uploaded document with source citations and confidence scores.', path: '/ask' },
          { icon: <Route size={20} />, title: 'ActionPath', desc: 'Generate a structured preparation guide with lawyer questions and official legal resources.', path: '/actionpath' },
          { icon: <BookOpen size={20} />, title: t.nav.sources, desc: 'Browse curated authoritative Indian legal portals — India Code, Supreme Court, NALSA, eCourts.', path: '/sources' },
          { icon: <Shield size={20} />, title: t.nav.security, desc: 'Understand how NyayaLens protects your data — PII redaction, prompt injection defense, session wipe.', path: '/privacy-security' },
        ].map((item, i) => (
          <button key={i} className="card" style={{ textAlign: 'left', cursor: 'pointer', border: '1px solid var(--border-color)' }} onClick={() => navigate(item.path)}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--color-accent)' }}>
              {item.icon}
              <h3 style={{ fontSize: '0.95rem' }}>{item.title}</h3>
            </div>
            <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', marginBottom: 0 }}>{item.desc}</p>
          </button>
        ))}
      </div>

      {/* Demo Quick Launch */}
      <div className="section-header">
        <ArrowRight size={18} className="section-header__icon" />
        <h2>Quick Demonstration Scenarios</h2>
      </div>
      <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', marginBottom: 16 }}>
        Load synthetic legal documents instantly to experience the full NyayaLens analysis pipeline. No real personal data is used.
      </p>
      <div className="demo-grid">
        <div className="demo-card" onClick={() => loadDemo('employment-agreement')} role="button" tabIndex={0} aria-label="Load Employment Dispute demo">
          <div className="demo-card__title">⚖️ Employment Dispute</div>
          <div className="demo-card__desc">Employment Agreement vs Termination Notice — detects notice period mismatch, void non-compete, missing severance.</div>
          {loading === 'employment-agreement' && <div className="loading-spinner" style={{padding: 8, fontSize: '0.75rem'}}>Loading & analyzing...</div>}
        </div>
        <div className="demo-card" onClick={() => loadDemo('rental-agreement')} role="button" tabIndex={0} aria-label="Load Rental Escalation demo">
          <div className="demo-card__title">🏠 Rental Escalation</div>
          <div className="demo-card__desc">11-Month Lease vs 25% rent increase notice — detects breach of 5% cap clause.</div>
          {loading === 'rental-agreement' && <div className="loading-spinner" style={{padding: 8, fontSize: '0.75rem'}}>Loading & analyzing...</div>}
        </div>
        <div className="demo-card" onClick={() => loadDemo('mutual-nda')} role="button" tabIndex={0} aria-label="Load NDA Comparison demo">
          <div className="demo-card__title">🔒 NDA Comparison</div>
          <div className="demo-card__desc">Standard Mutual NDA vs Unilateral Vendor NDA — detects perpetual secrecy and unlimited damages.</div>
          {loading === 'mutual-nda' && <div className="loading-spinner" style={{padding: 8, fontSize: '0.75rem'}}>Loading & analyzing...</div>}
        </div>
      </div>
    </div>
  );
}
