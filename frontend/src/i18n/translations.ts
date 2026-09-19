export type Language = 'en' | 'hi' | 'te';

export interface TranslationDict {
  appName: string;
  tagline: string;
  disclaimer: string;
  nav: {
    dashboard: string;
    upload: string;
    analysis: string;
    compare: string;
    ask: string;
    actionpath: string;
    sources: string;
    security: string;
    howItWorks: string;
    privacyMode: string;
    explainSimply: string;
  };
  actions: {
    analyze: string;
    uploadDoc: string;
    selectFile: string;
    loadDemo: string;
    askQuestion: string;
    compareDocs: string;
    printActionPath: string;
    wipeSession: string;
    viewEvidence: string;
    jumpToSource: string;
  };
  statuses: {
    verified: string;
    inferred: string;
    notFound: string;
    needsReview: string;
  };
  severities: {
    low: string;
    medium: string;
    high: string;
    critical: string;
  };
}

export const translations: Record<Language, TranslationDict> = {
  en: {
    appName: 'NyayaLens',
    tagline: 'Understand the law. Verify the evidence. Know your next step.',
    disclaimer: 'Legal Information & Document Assistance Notice: NyayaLens is an informational tool, not a law firm, and does not provide legal advice. All document conclusions are grounded in the uploaded text and should be reviewed with qualified legal counsel.',
    nav: {
      dashboard: 'Dashboard',
      upload: 'Upload & Ingest',
      analysis: 'Analysis Workspace',
      compare: 'Compare Documents',
      ask: 'Ask My Document',
      actionpath: 'ActionPath',
      sources: 'Legal Sources',
      security: 'Privacy & Security',
      howItWorks: 'How AI Works',
      privacyMode: 'Privacy Mode (PII Redaction)',
      explainSimply: 'Explain Simply',
    },
    actions: {
      analyze: 'Analyze Document',
      uploadDoc: 'Upload Legal Document',
      selectFile: 'Choose PDF or Text File',
      loadDemo: 'Load Demonstration Scenario',
      askQuestion: 'Ask Question',
      compareDocs: 'Compare Documents',
      printActionPath: 'Export / Print ActionPath',
      wipeSession: 'Delete All Session Data',
      viewEvidence: 'Inspect Evidence',
      jumpToSource: 'Highlight in Document',
    },
    statuses: {
      verified: 'VERIFIED',
      inferred: 'INFERRED',
      notFound: 'NOT FOUND',
      needsReview: 'NEEDS REVIEW',
    },
    severities: {
      low: 'LOW',
      medium: 'MEDIUM',
      high: 'HIGH',
      critical: 'CRITICAL',
    },
  },
  hi: {
    appName: 'न्यायलेंस (NyayaLens)',
    tagline: 'कानून समझें। साक्ष्य सत्यापित करें। अगला कदम जानें।',
    disclaimer: 'कानूनी सूचना सूचना: न्यायालेंस केवल दस्तावेज़ विश्लेषण और कानूनी जानकारी प्रदान करता है, कानूनी सलाह नहीं। सभी निष्कर्ष दस्तावेज़ पर आधारित हैं और इनकी समीक्षा किसी पेशेवर वकील से अवश्य कराएं।',
    nav: {
      dashboard: 'डैशबोर्ड',
      upload: 'अपलोड एवं विश्लेषण',
      analysis: 'विश्लेषण कार्यक्षेत्र',
      compare: 'दस्तावेज़ तुलना',
      ask: 'दस्तावेज़ से पूछें',
      actionpath: 'एक्शन पाथ (अगला कदम)',
      sources: 'आधिकारिक स्रोत',
      security: 'गोपनीयता एवं सुरक्षा',
      howItWorks: 'एआई कैसे काम करता है',
      privacyMode: 'गोपनीयता मोड (पीआईआई छुपाएं)',
      explainSimply: 'सरल भाषा में समझें',
    },
    actions: {
      analyze: 'विश्लेषण करें',
      uploadDoc: 'दस्तावेज़ अपलोड करें',
      selectFile: 'फ़ाइल चुनें (पीडीएफ या टेक्स्ट)',
      loadDemo: 'डेमो लोड करें',
      askQuestion: 'प्रश्न पूछें',
      compareDocs: 'दस्तावेज़ों की तुलना करें',
      printActionPath: 'एक्शन पाथ प्रिंट करें',
      wipeSession: 'सत्र डेटा हटाएं',
      viewEvidence: 'साक्ष्य जांचें',
      jumpToSource: 'दस्तावेज़ में देखें',
    },
    statuses: {
      verified: 'सत्यापित (VERIFIED)',
      inferred: 'अनुमानित (INFERRED)',
      notFound: 'नहीं मिला (NOT FOUND)',
      needsReview: 'पुनरावलोकन आवश्यक (NEEDS REVIEW)',
    },
    severities: {
      low: 'कम (LOW)',
      medium: 'मध्यम (MEDIUM)',
      high: 'उच्च (HIGH)',
      critical: 'गंभीर (CRITICAL)',
    },
  },
  te: {
    appName: 'న్యాయలెన్స్ (NyayaLens)',
    tagline: 'చట్టాన్ని అర్థం చేసుకోండి. సాక్ష్యాన్ని సరిచూసుకోండి. తదుపరి అడుగు తెలుసుకోండి.',
    disclaimer: 'చట్టపరమైన సమాచార నోటీసు: న్యాయలెన్స్ పత్రాల విశ్లేషణ మరియు సమాచారం కొరకు మాత్రమే, న్యాయ సలహా కాదు. అన్ని ముగింపులు మీ పత్రం ఆధారంగా రూపొందించబడ్డాయి.',
    nav: {
      dashboard: 'డాష్‌బోర్డ్',
      upload: 'అప్‌లోడ్ చేయండి',
      analysis: 'విశ్లేషణ స్థలం',
      compare: 'పత్రాల పోలిక',
      ask: 'ప్రశ్నించండి',
      actionpath: 'యాక్షన్ పాత్',
      sources: 'అధికారిక వనరులు',
      security: 'భద్రత మరియు గోప్యత',
      howItWorks: 'ఏఐ ఎలా పనిచేస్తుంది',
      privacyMode: 'గోప్యతా మోడ్',
      explainSimply: 'సరళంగా వివరించు',
    },
    actions: {
      analyze: 'విశ్లేషించండి',
      uploadDoc: 'పత్రం అప్‌లోడ్ చేయండి',
      selectFile: 'ఫైల్ ఎంచుకోండి',
      loadDemo: 'డెమో లోడ్ చేయండి',
      askQuestion: 'ప్రశ్న అడగండి',
      compareDocs: 'పోల్చండి',
      printActionPath: 'ప్రింట్ చేయండి',
      wipeSession: 'డేటా తొలగించండి',
      viewEvidence: 'సాక్ష్యం చూడండి',
      jumpToSource: 'మూలాన్ని గుర్తించండి',
    },
    statuses: {
      verified: 'ధృవీకరించబడింది (VERIFIED)',
      inferred: 'ఊహించబడింది (INFERRED)',
      notFound: 'కనుగొనబడలేదు (NOT FOUND)',
      needsReview: 'సమీక్ష అవసరం (NEEDS REVIEW)',
    },
    severities: {
      low: 'తక్కువ (LOW)',
      medium: 'మధ్యస్థం (MEDIUM)',
      high: 'అధికం (HIGH)',
      critical: 'తీవ్రమైనది (CRITICAL)',
    },
  },
};

export const plainLanguageDictionary: Record<string, string> = {
  'indemnification': 'You may have to cover financial losses, legal costs, or damages for the other party.',
  'non-compete': 'A rule blocking you from working for a competitor or starting a similar business after leaving.',
  'unilateral termination': 'One party can cancel the contract alone, potentially without giving equal rights to the other.',
  'arbitration': 'Resolving a dispute through a private neutral decider (arbitrator) instead of going to public court.',
  'liquidated damages': 'A predetermined penalty fee written into the contract that you must pay if a breach occurs.',
  'evergreen clause': 'The contract automatically renews forever unless you remember to cancel it in writing beforehand.',
  'severance': 'Compensation paid to an employee when employment is terminated without personal fault.',
  'jurisdiction': 'The specific city or court that has legal authority to hear and decide any dispute.',
  'force majeure': 'Unforeseen natural disasters or events (like floods or war) that excuse parties from contract duties.',
  'limitation of liability': 'A monetary cap on the maximum compensation one party can collect if something goes wrong.',
};
