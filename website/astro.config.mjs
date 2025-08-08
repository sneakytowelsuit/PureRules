// @ts-nocheck
import { defineConfig } from 'astro/config';
import path from 'node:path';
import { spawn } from 'node:child_process';

function JavaDocSyncPlugin() {
  return {
    name: 'purerules-javadoc-sync',
    configureServer(server) {
      const runSync = () => {
        const child = spawn(process.execPath, ['scripts/sync-javadoc.mjs'], {
          cwd: process.cwd(),
          stdio: 'inherit',
        });
        child.on('exit', (code) => {
          if (code === 0) server.ws.send({ type: 'full-reload' });
        });
      };
      // initial run on startup
      runSync();
      const javaDir = path.resolve(process.cwd(), '../src/main/java');
      server.watcher.add(javaDir);
      let timer;
      server.watcher.on('all', (event, file) => {
        if (!file.endsWith('.java')) return;
        clearTimeout(timer);
        timer = setTimeout(runSync, 150);
      });
    },
  };
}

// Normalize base path for Astro (must be a pathname with leading & trailing slash)
const rawBase = process.env.PAGES_BASE_PATH || '/';
let base = rawBase;
try {
  // If an absolute URL is provided, extract just the pathname
  const u = new URL(rawBase);
  base = u.pathname;
} catch {
  // If not a valid absolute URL, keep as-is
}
if (!base.startsWith('/')) base = '/' + base;
if (!base.endsWith('/')) base += '/';

// Compute site for GitHub Pages. Example: https://<owner>.github.io[/<repo>/]
const owner = process.env.GITHUB_REPOSITORY_OWNER || 'sneakytowelsuit';
const origin = `https://${owner}.github.io`;
const site = origin.replace(/\/$/, '') + (base === '/' ? '' : base);

// https://astro.build/config
export default defineConfig({
  site,
  base,
  integrations: [],
  markdown: {
    shikiConfig: {
      themes: {
        light: 'github-light',
        dark: 'github-dark',
      },
    },
  },
  vite: {
    server: {
      fs: { allow: ['..'] }
    },
    plugins: [JavaDocSyncPlugin()],
  }
});
