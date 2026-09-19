# NyayaLens Accessibility & Inclusive Design (WCAG 2.1 AA)

Accessibility is foundational to legal empowerment. NyayaLens is engineered to meet and exceed **Web Content Accessibility Guidelines (WCAG) 2.1 Level AA** standards.

---

## 1. Core Accessibility Features

### 1.1 Skip Navigation Link
- A dedicated `<a href="#main-content" className="skip-link">Skip to main content</a>` element is positioned at the top of the DOM.
- Visually hidden until keyboard-focused via `Tab`, allowing screen reader and keyboard-only users to bypass navigation headers directly to the analysis workspace.

### 1.2 Contrast & Typography Tokens
- **Primary Text**: Slate `#1a1a1a` on Ivory `#fafaf9` yields a **15.4:1 contrast ratio**, far exceeding the WCAG AA minimum requirement of 4.5:1.
- **Accents & Severity Badges**: Carefully chosen HSL tokens ensure that critical `#991b1b`, high `#c2410c`, medium `#a16207`, and verified `#166534` badges maintain clear legibility against tinted pastel backgrounds.
- **Dynamic Font Scaling**: All typography uses relative units (`rem`), respecting user system font scale preferences.

### 1.3 Semantic Landmarks & ARIA Support
- Semantic HTML5 structure: `<nav role="navigation">`, `<main id="main-content" role="main">`, `<aside aria-label="Evidence Inspector">`, and `<aside aria-label="Document Sections">`.
- Interactive cards provide explicit `role="button"` and `tabIndex={0}` attributes with keyboard enter/spacebar handlers.
- Dynamic status banners use `role="status"` and `aria-live="polite"` for non-intrusive screen reader announcements.

### 1.4 Multilingual Inclusivity (en / hi / te)
- Integrated translations supporting India's primary administrative and regional languages: English, Hindi (हिन्दी), and Telugu (తెలుగు).
- Language toggling is accessible via standard radio button groups with `aria-checked` attributes.

### 1.5 Plain-Language Cognitive Accessibility
- Legal jargon creates cognitive barriers for non-lawyers.
- The **"Explain Simply"** mode automatically identifies statutory terms (e.g., *indemnity*, *liquidated damages*, *arbitration*, *jurisdiction*) and renders plain-language contextual explanations inline.

### 1.6 Reduced Motion & Focus Rings
- The stylesheet strictly honors the user's OS preference:
  ```css
  @media (prefers-reduced-motion: reduce) {
    *, *::before, *::after {
      animation-duration: 0.01ms !important;
      transition-duration: 0.01ms !important;
    }
  }
  ```
- All interactive controls feature high-visibility focus indicators:
  ```css
  :focus-visible {
    outline: 2px solid var(--color-accent);
    outline-offset: 2px;
  }
  ```
