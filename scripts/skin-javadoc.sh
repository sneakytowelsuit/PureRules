#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="${1:-build/docs/javadoc}"
if [[ ! -d "$OUT_DIR" ]]; then
  echo "Javadoc output directory not found: $OUT_DIR" >&2
  exit 1
fi

# Locate stylesheet.css produced by javadoc
CSS_FILE=""
if [[ -f "$OUT_DIR/stylesheet.css" ]]; then
  CSS_FILE="$OUT_DIR/stylesheet.css"
else
  # Fallback: search recursively (future-proof for layout changes)
  CSS_FILE=$(find "$OUT_DIR" -type f -name 'stylesheet.css' | head -n 1 || true)
fi

if [[ -z "$CSS_FILE" ]]; then
  echo "Could not locate stylesheet.css under: $OUT_DIR" >&2
  exit 1
fi

# Backup original stylesheet once
if [[ ! -f "${CSS_FILE}.orig" ]]; then
  cp "$CSS_FILE" "${CSS_FILE}.orig"
fi

# Append neo-brutalist overrides with !important so they take precedence
cat >> "$CSS_FILE" <<'CSS'
/* === Neo-brutalist skin (appended by scripts/skin-javadoc.sh) === */
:root {
  --nb-bg: #fefefe;
  --nb-fg: #111;
  --nb-accent: #ff3b3b;
  --nb-accent-2: #2b6aff;
  --nb-muted: #e9e9e9;
  --nb-shadow: 6px 6px 0 rgba(0,0,0,0.9);
}

html, body {
  background: var(--nb-bg) !important;
  color: var(--nb-fg) !important;
}

body, .contentContainer, .header, .footer, .overviewSummary, .summary, .details {
  background: var(--nb-bg) !important;
  box-shadow: none !important;
}

/* Typography */
body, td, th, code, pre {
  font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, Noto Sans, Arial, "Apple Color Emoji", "Segoe UI Emoji" !important;
}
pre, code { font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace !important; }

/* Links */
a, a:visited { color: var(--nb-accent) !important; text-decoration: underline !important; }
a:hover, a:focus { color: var(--nb-accent-2) !important; }

/* Brutalist borders */
.header, .subTitle, .topNav, .bottomNav, .navBarCell1Rev, .summary, .details, .contentContainer, .overviewSummary,
.blockList, .blockListLast, .block, table, th, td, .description, .memberSummary, .inheritance, .typeSummary {
  border: 3px solid var(--nb-fg) !important;
  border-radius: 0 !important;
  background: var(--nb-bg) !important;
}

/* Cards look */
.block, .summary, .details, .memberSummary, .overviewSummary {
  box-shadow: var(--nb-shadow) !important;
  padding: 1rem !important;
  margin: 1rem 0 !important;
}

/* Navigation bar */
.topNav, .bottomNav, .navList, .subNav {
  background: var(--nb-bg) !important;
  border: 3px solid var(--nb-fg) !important;
}

.navList li, .subNav li { margin-right: .6rem !important; }
.navBarCell1Rev, .skipNav {
  background: var(--nb-accent) !important;
  color: white !important;
  border: 3px solid var(--nb-fg) !important;
  text-transform: uppercase;
  font-weight: 800;
  letter-spacing: .03em;
}

/* Tables */
table, th, td { border: 3px solid var(--nb-fg) !important; }
th { background: var(--nb-muted) !important; }
tr:nth-child(even) td { background: #fafafa !important; }

/* Code blocks */
pre {
  border: 3px solid var(--nb-fg) !important;
  background: #fff !important;
  padding: 1rem !important;
}

/* Search box and inputs (if present) */
input, select, button {
  border: 3px solid var(--nb-fg) !important;
  border-radius: 0 !important;
  background: var(--nb-bg) !important;
  color: var(--nb-fg) !important;
}
button, .button, .navBarCell1Rev {
  box-shadow: var(--nb-shadow) !important;
}

/* Headings */
h1, h2, h3, h4, h5 { text-transform: none !important; }
h1, h2 { border-bottom: 3px solid var(--nb-fg) !important; padding-bottom: .25rem !important; }

/* Breadcrumbs/Title area */
.header .title { font-weight: 900 !important; }

/* Hide redundant thin borders from base theme */
hr { border-top: 3px solid var(--nb-fg) !important; }

/* Make badges/tags pop */
.deprecatedLabel, .sinceLabel, .previewLabel {
  border: 3px solid var(--nb-fg) !important;
  background: var(--nb-accent-2) !important;
  color: #fff !important;
}
/* === End neo-brutalist skin === */
CSS

echo "Applied neo-brutalist skin to: $CSS_FILE"
