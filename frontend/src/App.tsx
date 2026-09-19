import { useState, useCallback, createContext, useContext } from 'react';
import { Routes, Route, NavLink } from 'react-router-dom';
import { Scale, LayoutDashboard, Upload, Search, GitCompare, MessageCircleQuestion, Route as RouteIcon, BookOpen, Shield, Cpu } from 'lucide-react';
import { Language, translations, TranslationDict } from './i18n/translations';
import { DocumentUploadResponse, AnalysisResult, LegalDocument } from './types';
import DashboardPage from './pages/DashboardPage';
import UploadPage from './pages/UploadPage';
import AnalysisPage from './pages/AnalysisPage';
import ComparePage from './pages/ComparePage';
import AskPage from './pages/AskPage';
import ActionPathPage from './pages/ActionPathPage';
import SourcesPage from './pages/SourcesPage';
import SecurityPage from './pages/SecurityPage';
import HowItWorksPage from './pages/HowItWorksPage';

// Application State Context
interface AppState {
  documents: DocumentUploadResponse[];
  setDocuments: React.Dispatch<React.SetStateAction<DocumentUploadResponse[]>>;
  currentDocument: LegalDocument | null;
  setCurrentDocument: React.Dispatch<React.SetStateAction<LegalDocument | null>>;
  currentAnalysis: AnalysisResult | null;
  setCurrentAnalysis: React.Dispatch<React.SetStateAction<AnalysisResult | null>>;
  language: Language;
  setLanguage: (lang: Language) => void;
  t: TranslationDict;
  privacyMode: boolean;
  setPrivacyMode: React.Dispatch<React.SetStateAction<boolean>>;
  plainLanguage: boolean;
  setPlainLanguage: React.Dispatch<React.SetStateAction<boolean>>;
}

export const AppContext = createContext<AppState | null>(null);
export const useApp = () => {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error('useApp must be used within AppContext');
  return ctx;
};

export default function App() {
  const [documents, setDocuments] = useState<DocumentUploadResponse[]>([]);
  const [currentDocument, setCurrentDocument] = useState<LegalDocument | null>(null);
  const [currentAnalysis, setCurrentAnalysis] = useState<AnalysisResult | null>(null);
  const [language, setLanguageState] = useState<Language>('en');
  const [privacyMode, setPrivacyMode] = useState(true);
  const [plainLanguage, setPlainLanguage] = useState(false);

  const t = translations[language];
  const setLanguage = useCallback((lang: Language) => setLanguageState(lang), []);

  const appState: AppState = {
    documents, setDocuments,
    currentDocument, setCurrentDocument,
    currentAnalysis, setCurrentAnalysis,
    language, setLanguage, t,
    privacyMode, setPrivacyMode,
    plainLanguage, setPlainLanguage,
  };

  return (
    <AppContext.Provider value={appState}>
      <div className="app-layout">
        {/* Skip Navigation for Accessibility */}
        <a href="#main-content" className="skip-link">Skip to main content</a>

        {/* Legal Disclaimer Banner */}
        <div className="disclaimer-banner" role="status">
          {t.disclaimer}
        </div>

        {/* Navigation */}
        <nav className="app-nav" role="navigation" aria-label="Primary navigation">
          <NavLink to="/" className="app-nav__logo" aria-label="NyayaLens Home">
            <Scale className="app-nav__logo-icon" size={24} />
            <span className="app-nav__logo-text">{t.appName}</span>
          </NavLink>

          <ul className="app-nav__links">
            <li><NavLink to="/" end className={({isActive}) => isActive ? 'active' : ''}><LayoutDashboard size={14} />{t.nav.dashboard}</NavLink></li>
            <li><NavLink to="/upload" className={({isActive}) => isActive ? 'active' : ''}><Upload size={14} />{t.nav.upload}</NavLink></li>
            <li><NavLink to="/analysis" className={({isActive}) => isActive ? 'active' : ''}><Search size={14} />{t.nav.analysis}</NavLink></li>
            <li><NavLink to="/compare" className={({isActive}) => isActive ? 'active' : ''}><GitCompare size={14} />{t.nav.compare}</NavLink></li>
            <li><NavLink to="/ask" className={({isActive}) => isActive ? 'active' : ''}><MessageCircleQuestion size={14} />{t.nav.ask}</NavLink></li>
            <li><NavLink to="/actionpath" className={({isActive}) => isActive ? 'active' : ''}><RouteIcon size={14} />{t.nav.actionpath}</NavLink></li>
            <li><NavLink to="/sources" className={({isActive}) => isActive ? 'active' : ''}><BookOpen size={14} />{t.nav.sources}</NavLink></li>
            <li><NavLink to="/privacy-security" className={({isActive}) => isActive ? 'active' : ''}><Shield size={14} />{t.nav.security}</NavLink></li>
            <li><NavLink to="/how-it-works" className={({isActive}) => isActive ? 'active' : ''}><Cpu size={14} />{t.nav.howItWorks}</NavLink></li>
          </ul>

          <div className="app-nav__controls">
            {/* Plain Language Toggle */}
            <label className="toggle" title={t.nav.explainSimply}>
              <span className={`toggle__track ${plainLanguage ? 'toggle__track--active' : ''}`}>
                <span className="toggle__knob" />
              </span>
              <span>{t.nav.explainSimply}</span>
              <input type="checkbox" checked={plainLanguage} onChange={() => setPlainLanguage(v => !v)} style={{display:'none'}} />
            </label>

            {/* Language Switcher */}
            <div className="lang-switcher" role="radiogroup" aria-label="Language">
              {(['en', 'hi', 'te'] as Language[]).map(lang => (
                <button key={lang}
                  className={`lang-switcher__btn ${language === lang ? 'lang-switcher__btn--active' : ''}`}
                  onClick={() => setLanguage(lang)}
                  role="radio"
                  aria-checked={language === lang}
                  aria-label={lang === 'en' ? 'English' : lang === 'hi' ? 'Hindi' : 'Telugu'}
                >
                  {lang === 'en' ? 'EN' : lang === 'hi' ? 'हि' : 'తె'}
                </button>
              ))}
            </div>
          </div>
        </nav>

        {/* Main Content */}
        <main className="app-main" id="main-content" role="main">
          <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/upload" element={<UploadPage />} />
            <Route path="/analysis" element={<AnalysisPage />} />
            <Route path="/compare" element={<ComparePage />} />
            <Route path="/ask" element={<AskPage />} />
            <Route path="/actionpath" element={<ActionPathPage />} />
            <Route path="/sources" element={<SourcesPage />} />
            <Route path="/privacy-security" element={<SecurityPage />} />
            <Route path="/how-it-works" element={<HowItWorksPage />} />
          </Routes>
        </main>
      </div>
    </AppContext.Provider>
  );
}
