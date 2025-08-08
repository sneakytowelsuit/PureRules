# PureRules Website

Astro-powered documentation site for PureRules.

- Neo-brutalist design
- Markdown content collections for docs and reference
- Optional sync step to import JavaDoc into reference pages

## Commands

- npm run dev — start dev server at localhost:4321
- npm run build — build site to dist/
- npm run preview — preview built site
- npm run sync — generate reference pages from JavaDoc (see below)

## JavaDoc sync

This monorepo includes a script to generate JavaDoc JSON and convert it into Markdown reference pages under `src/content/reference`. It parses Java source in `../src/main/java`.

Run:

```
npm run sync
```

If you change Java code, rerun the sync.
