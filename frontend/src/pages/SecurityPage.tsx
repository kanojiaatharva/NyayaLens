import { useState } from 'react';
import {
  Shield,
  Lock,
  Eye,
  Trash2,
  CheckCircle2,
  FileCheck,
  Server,
  Zap,
} from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';

export default function SecurityPage() {
  const { privacyMode, setPrivacyMode, setDocuments, setCurrentDocument, setCurrentAnalysis, t } = useApp();

  const [sampleText, setSampleText] = useState(
    'Employee Name: Rajesh Kumar (PAN: ABCDE1234F, Aadhaar: 5432 1098 7654)\n' +
    'Contact: rajesh.kumar@example.com or +91 98765 43210\n' +
    'Account: Bank of India IFSC BKID0001234 A/C 987654321098765\n' +
    'Clause 8.2: The Company may terminate employment by giving 60 days notice.'
  );

  const [wipeStatus, setWipeStatus] = useState<string | null>(null);

  // Client-side regex simulation matching backend PiiAnonymizer
  const simulateRedaction = (text: string) => {
    return text
      .replace(/\b[2-9]\d{3}\s?\d{4}\s?\d{4}\b/g, '[REDACTED_AADHAAR]')
      .replace(/\b[A-Z]{5}[0-9]{4}[A-Z]\b/g, '[REDACTED_PAN]')
      .replace(/(\+91[\s-]?)?[6-9]\d{9}\b/g, '[REDACTED_PHONE]')
      .replace(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g, '[REDACTED_EMAIL]')
      .replace(/\b\d{9,18}\b/g, '[REDACTED_ACCOUNT]');
  };

  const handleWipeAll = async () => {
    if (window.confirm('Are you sure you want to permanently purge all uploaded documents, extracted clauses, and session caches?')) {
      try {
        await api.deleteAllSessionData();
        setDocuments([]);
        setCurrentDocument(null);
        setCurrentAnalysis(null);
        setWipeStatus('Session memory completely wiped. All in-memory document state destroyed.');
      } catch (err: unknown) {
        setWipeStatus('Failed to wipe session: ' + (err instanceof Error ? err.message : 'Unknown error'));
      }
    }
  };

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto' }}>
      <div className="section-header">
        <Shield className="section-header__icon" size={24} />
        <div>
          <h2>{t.nav.security}</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
            NyayaLens enterprise threat model, zero-retention privacy architecture, and cryptographic sanitization.
          </p>
        </div>
      </div>

      {wipeStatus && (
        <div style={{ padding: 12, background: 'var(--color-low-bg)', border: '1px solid var(--color-low-border)', borderRadius: 'var(--radius-md)', color: 'var(--color-low)', fontSize: '0.85rem', marginBottom: 16 }}>
          <CheckCircle2 size={16} style={{ display: 'inline', marginRight: 6, verticalAlign: 'text-bottom' }} />
          {wipeStatus}
        </div>
      )}

      {/* Global Privacy Mode Switcher */}
      <div className="card" style={{ marginBottom: 24, borderLeft: '4px solid var(--color-accent)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12 }}>
          <div>
            <h3 style={{ fontSize: '1rem', marginBottom: 4 }}>System Privacy Mode</h3>
            <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', margin: 0 }}>
              When enabled, all incoming legal documents are sanitized with deterministic regex masking before AI inference.
            </p>
          </div>
          <label className="toggle">
            <span className={`toggle__track ${privacyMode ? 'toggle__track--active' : ''}`}>
              <span className="toggle__knob" />
            </span>
            <span style={{ fontWeight: 600 }}>{privacyMode ? 'PII Sanitization ENABLED' : 'Raw Document Mode'}</span>
            <input
              type="checkbox"
              checked={privacyMode}
              onChange={() => setPrivacyMode(v => !v)}
              style={{ display: 'none' }}
            />
          </label>
        </div>
      </div>

      {/* Interactive Live PII Sanitization Simulator */}
      <div className="card" style={{ marginBottom: 28 }}>
        <div className="card__header">
          <h3 className="card__title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Eye size={18} color="var(--color-accent)" /> Interactive PII Sanitization Simulator
          </h3>
          <span className="badge badge--verified">Live Client-Side Preview</span>
        </div>
        <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', marginBottom: 12 }}>
          Edit the left pane to test live masking of Indian national identifiers (Aadhaar, PAN), contact info, and bank numbers:
        </p>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
          <div>
            <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-tertiary)', display: 'block', marginBottom: 4 }}>
              RAW USER INPUT / DOCUMENT TEXT:
            </label>
            <textarea
              value={sampleText}
              onChange={e => setSampleText(e.target.value)}
              rows={7}
              style={{
                width: '100%',
                padding: 10,
                fontSize: '0.8rem',
                fontFamily: 'monospace',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--radius-sm)',
                background: '#ffffff',
                resize: 'vertical',
              }}
            />
          </div>

          <div>
            <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-verified)', display: 'block', marginBottom: 4 }}>
              SANITIZED PAYLOAD DISPATCHED TO AI:
            </label>
            <div
              style={{
                width: '100%',
                minHeight: 140,
                padding: 10,
                fontSize: '0.8rem',
                fontFamily: 'monospace',
                border: '1px solid var(--color-verified)',
                borderRadius: 'var(--radius-sm)',
                background: 'var(--color-verified-bg)',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {simulateRedaction(sampleText)}
            </div>
          </div>
        </div>
      </div>

      {/* Threat Model Breakdown Cards */}
      <h3 style={{ fontSize: '1.1rem', marginBottom: 14 }}>Enterprise Threat Model & Safeguards</h3>

      <div className="card__grid" style={{ marginBottom: 28 }}>
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--color-accent)' }}>
            <Lock size={18} />
            <h4 style={{ fontSize: '0.9rem', margin: 0 }}>Prompt Injection Defense</h4>
          </div>
          <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', lineHeight: 1.5, margin: 0 }}>
            Untrusted document contents are enclosed inside strict boundary delimiters (<code>&lt;UNTRUSTED_DOCUMENT_CONTENT&gt;</code>) with hard instructions to ignore any embedded directives, roleplay requests, or system override attempts.
          </p>
        </div>

        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--color-accent)' }}>
            <Server size={18} />
            <h4 style={{ fontSize: '0.9rem', margin: 0 }}>Zero-Retention Session Memory</h4>
          </div>
          <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', lineHeight: 1.5, margin: 0 }}>
            Documents exist exclusively in volatile memory caches with eviction policies. No files are saved to permanent physical disks, ensuring full compliance with the Digital Personal Data Protection (DPDP) Act, 2023.
          </p>
        </div>

        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--color-accent)' }}>
            <FileCheck size={18} />
            <h4 style={{ fontSize: '0.9rem', margin: 0 }}>Magic Byte Validation</h4>
          </div>
          <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', lineHeight: 1.5, margin: 0 }}>
            Every upload is validated against true file signatures (e.g. <code>%PDF-</code>) rather than superficial file extensions, preventing malicious script or executable injection.
          </p>
        </div>

        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--color-accent)' }}>
            <Zap size={18} />
            <h4 style={{ fontSize: '0.9rem', margin: 0 }}>Token Bucket Rate Limiting</h4>
          </div>
          <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', lineHeight: 1.5, margin: 0 }}>
            Client IPs are throttled through in-memory token buckets (5 heavy analyses/min, 20 Q&A/min) to prevent denial-of-service and API key exhaustion.
          </p>
        </div>
      </div>

      {/* Emergency Wipe Action */}
      <div className="card" style={{ borderColor: 'var(--color-critical-border)', background: 'var(--color-critical-bg)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12 }}>
          <div>
            <h4 style={{ color: 'var(--color-critical)', margin: 0 }}>Emergency Session Wipe</h4>
            <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', margin: '4px 0 0' }}>
              Purge all in-memory document text, extracted clauses, risk findings, and Q&A history immediately.
            </p>
          </div>
          <button className="btn btn--danger" onClick={handleWipeAll}>
            <Trash2 size={16} /> Purge All Session Data Now
          </button>
        </div>
      </div>
    </div>
  );
}
