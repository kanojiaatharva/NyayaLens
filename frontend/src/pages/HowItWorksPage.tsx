import { Cpu, Scale, CheckCircle2, Zap, Layers, Database } from 'lucide-react';
import { useApp } from '../App';

export default function HowItWorksPage() {
  const { t } = useApp();

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto' }}>
      <div className="section-header">
        <Cpu className="section-header__icon" size={24} />
        <div>
          <h2>{t.nav.howItWorks}</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
            Under the hood: Dual Gemini AI orchestration, deterministic verification layers, and legal boundary guarantees.
          </p>
        </div>
      </div>

      {/* Hero Overview */}
      <div className="card" style={{ marginBottom: 24, borderLeft: '4px solid var(--color-accent)' }}>
        <h3 style={{ fontSize: '1.05rem', marginBottom: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Scale size={20} color="var(--color-accent)" /> The NyayaLens Grounding Philosophy
        </h3>
        <p style={{ fontSize: '0.88rem', lineHeight: 1.6, color: 'var(--color-text-primary)' }}>
          Most legal AI tools suffer from dangerous hallucinations, fabricating precedents or asserting contractual rights that do not exist. <strong>NyayaLens introduces a zero-trust verification pipeline</strong>: every generated assertion is deterministically re-evaluated against the raw document source. If an AI claim cannot be grounded in an exact page and clause, it is flagged as <code>NEEDS REVIEW</code> or <code>INFERRED</code>.
        </p>
      </div>

      {/* Dual AI Model Strategy */}
      <h3 style={{ fontSize: '1.1rem', marginBottom: 14 }}>Dual-AI Orchestration Strategy</h3>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 16, marginBottom: 28 }}>
        {/* Gemini 3.8 Flash */}
        <div className="ai-pipeline-card">
          <div className="ai-pipeline-card__header">
            <Zap size={22} color="var(--color-high)" />
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <h4 style={{ margin: 0, fontSize: '1rem' }}>Gemini 3.8 Flash</h4>
                <span className="badge badge--high">High Speed • Low Latency</span>
              </div>
              <span style={{ fontSize: '0.75rem', color: 'var(--color-text-tertiary)' }}>Model ID: gemini-3.8-flash</span>
            </div>
          </div>
          <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', marginBottom: 12 }}>
            Specialized for sub-second streaming, rapid clause chunking, plain-language translation, and conversational Q&A.
          </p>
          <ul className="ai-pipeline-card__features">
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Instant clause extraction and classification</li>
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Plain-language simplification and definitions</li>
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Fast conversational answering grounded in document context</li>
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Multilingual synthesis (English, Hindi, Telugu)</li>
          </ul>
        </div>

        {/* Gemini 3.1 Pro Preview */}
        <div className="ai-pipeline-card">
          <div className="ai-pipeline-card__header">
            <Layers size={22} color="var(--color-accent)" />
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <h4 style={{ margin: 0, fontSize: '1rem' }}>Gemini 3.1 Pro Preview</h4>
                <span className="badge badge--inferred">Deep Reasoning • Semantic Diff</span>
              </div>
              <span style={{ fontSize: '0.75rem', color: 'var(--color-text-tertiary)' }}>Model ID: gemini-3.1-pro-preview</span>
            </div>
          </div>
          <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', marginBottom: 12 }}>
            Specialized for complex multi-document comparison, contradictory obligation detection, and structured risk synthesis.
          </p>
          <ul className="ai-pipeline-card__features">
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Cross-document semantic delta and contradiction reasoning</li>
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> High-confidence clause omission detection (what was omitted)</li>
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Structured JSON Schema enforcement without hallucinations</li>
            <li><CheckCircle2 size={14} color="var(--color-verified)" /> Strategic lawyer question generation for ActionPath</li>
          </ul>
        </div>
      </div>

      {/* 4-Stage Verification Pipeline */}
      <h3 style={{ fontSize: '1.1rem', marginBottom: 14 }}>The 4-Stage Verification Pipeline</h3>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 28 }}>
        {[
          {
            step: '01',
            title: 'Ingestion, Page Parsing & PII Sanitization',
            desc: 'Apache PDFBox extracts raw text with exact page boundaries and paragraph indices. Regex patterns for Aadhaar, PAN, phone numbers, and financial accounts redact sensitive personal data in-memory before transmission.',
          },
          {
            step: '02',
            title: 'Dual Rule Engine & LLM Analysis',
            desc: 'Deterministic heuristics detect high-risk patterns (short notice periods < 30 days, unilateral termination, unlimited indemnity). In parallel, Gemini evaluates complex nuances and categorizes obligations, deadlines, and financial terms.',
          },
          {
            step: '03',
            title: 'N-Gram & Fuzzy Evidence Verification Layer',
            desc: 'Every extracted claim is cross-matched against the document source text. The system calculates word overlap, n-gram containment, and semantic confidence scores, tagging each claim as VERIFIED, INFERRED, NOT_FOUND, or NEEDS_REVIEW.',
          },
          {
            step: '04',
            title: 'Legal Boundary & Procedural ActionPath Synthesis',
            desc: 'The platform generates a structured consultation brief for qualified legal counsel, mapping claims to statutory protections (e.g. Section 27 of Indian Contract Act for non-competes) and official legal aid portals (NALSA).',
          },
        ].map(item => (
          <div key={item.step} className="card" style={{ display: 'flex', gap: 16, alignItems: 'flex-start' }}>
            <div
              style={{
                width: 36,
                height: 36,
                borderRadius: '50%',
                background: 'var(--color-accent-bg)',
                color: 'var(--color-accent)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 700,
                fontSize: '0.9rem',
                flexShrink: 0,
              }}
            >
              {item.step}
            </div>
            <div>
              <h4 style={{ fontSize: '0.95rem', marginBottom: 4 }}>{item.title}</h4>
              <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', margin: 0, lineHeight: 1.5 }}>
                {item.desc}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Deterministic Offline Fallback Guarantee */}
      <div className="card" style={{ background: 'var(--color-bg-elevated)', borderColor: 'var(--color-border-strong)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
          <Database size={20} color="var(--color-accent)" />
          <h4 style={{ margin: 0, fontSize: '0.95rem' }}>100% Deterministic Fallback Guarantee for Evaluation</h4>
        </div>
        <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', margin: 0, lineHeight: 1.5 }}>
          To ensure evaluators and hackathon judges can review every workflow without network hiccups or missing API keys, NyayaLens includes a comprehensive offline fallback provider. If <code>GEMINI_API_KEY</code> is unconfigured or a network timeout occurs, the system smoothly serves realistic deterministic analysis payloads for all demonstration scenarios without ever crashing or returning blank screens.
        </p>
      </div>
    </div>
  );
}
