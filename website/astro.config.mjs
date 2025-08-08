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

const base = process.env.PAGES_BASE_PATH || '/';

// https://astro.build/config
export default defineConfig({
  site: 'https://example.com',
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
