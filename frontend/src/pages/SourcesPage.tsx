import { useState, useEffect } from 'react';
import { BookOpen, Search, ExternalLink, ShieldCheck, Scale, Landmark, Award } from 'lucide-react';
import { useApp } from '../App';
import { api } from '../api/client';
import { LegalResource } from '../types';

export default function SourcesPage() {
  const { t } = useApp();
  const [resources, setResources] = useState<LegalResource[]>([]);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.getLegalResources()
      .then(res => setResources(res))
      .catch(err => setError(err instanceof Error ? err.message : 'Failed to fetch legal resources.'))
      .finally(() => setLoading(false));
  }, []);

  const categories = ['ALL', 'STATUTORY', 'JUDICIAL', 'LEGAL_AID', 'CONSUMER_LABOR'];

  const filtered = resources.filter(res => {
    const matchesCat = categoryFilter === 'ALL' || res.category.toUpperCase() === categoryFilter;
    const matchesSearch =
      search === '' ||
      res.name.toLowerCase().includes(search.toLowerCase()) ||
      res.description.toLowerCase().includes(search.toLowerCase()) ||
      res.keyLegislationCovered.toLowerCase().includes(search.toLowerCase());
    return matchesCat && matchesSearch;
  });

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto' }}>
      <div className="section-header">
        <BookOpen className="section-header__icon" size={24} />
        <div>
          <h2>{t.nav.sources}</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', margin: 0 }}>
            Authoritative, official Indian legal repositories, apex court portals, and government legal aid institutions.
          </p>
        </div>
      </div>

      {/* Trust Banner */}
      <div className="card" style={{ marginBottom: 20, background: 'var(--color-accent-bg)', borderColor: 'var(--color-accent-border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <Scale size={28} color="var(--color-accent)" />
          <div>
            <h3 style={{ fontSize: '0.95rem', color: 'var(--color-accent)', marginBottom: 2 }}>
              Verified Sovereign Authority
            </h3>
            <p style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', margin: 0 }}>
              NyayaLens grounds legal concepts strictly in the laws enacted by the Parliament of India, judgments of the Supreme Court of India, and official statutory legal aid frameworks (NALSA under Legal Services Authorities Act, 1987).
            </p>
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', marginBottom: 20 }}>
        <div style={{ flex: 1, minWidth: 240, position: 'relative' }}>
          <input
            type="text"
            placeholder="Search resources, acts, or portals (e.g. Contract Act, NALSA, Supreme Court)..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{
              width: '100%',
              padding: '8px 30px 8px 12px',
              borderRadius: 'var(--radius-sm)',
              border: '1px solid var(--border-color)',
              fontSize: '0.85rem',
            }}
          />
          <Search size={16} style={{ position: 'absolute', right: 10, top: 10, color: 'var(--color-text-tertiary)' }} />
        </div>

        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
          {categories.map(cat => (
            <button
              key={cat}
              className={`btn btn--sm ${categoryFilter === cat ? 'btn--primary' : 'btn--secondary'}`}
              onClick={() => setCategoryFilter(cat)}
            >
              {cat.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {loading && <div className="loading-spinner">Loading official legal directory...</div>}
      {error && <div className="error-message">{error}</div>}

      {/* Resources List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {filtered.map(res => (
          <div key={res.id} className="resource-item">
            <div style={{ display: 'flex', alignItems: 'flex-start', paddingTop: 2 }}>
              {res.category === 'STATUTORY' && <Landmark size={22} color="var(--color-accent)" />}
              {res.category === 'JUDICIAL' && <Scale size={22} color="var(--color-accent)" />}
              {res.category === 'LEGAL_AID' && <ShieldCheck size={22} color="var(--color-verified)" />}
              {res.category === 'CONSUMER_LABOR' && <Award size={22} color="var(--color-high)" />}
            </div>

            <div className="resource-item__body" style={{ flex: 1 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 8, marginBottom: 4 }}>
                <h3 style={{ margin: 0 }}>{res.name}</h3>
                <div style={{ display: 'flex', gap: 6 }}>
                  <span className="badge badge--inferred">{res.category.replace('_', ' ')}</span>
                  <span className="badge badge--verified">{res.authorityLevel}</span>
                </div>
              </div>

              <p style={{ margin: '4px 0 8px', fontSize: '0.82rem', color: 'var(--color-text-secondary)', lineHeight: 1.5 }}>
                {res.description}
              </p>

              <div style={{ fontSize: '0.75rem', color: 'var(--color-text-tertiary)', marginBottom: 8 }}>
                <strong>Key Statutory Coverage:</strong> {res.keyLegislationCovered}
              </div>

              <a
                href={res.officialUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="resource-item__link"
              >
                Access Official Government Portal <ExternalLink size={12} />
              </a>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
