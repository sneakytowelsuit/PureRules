#!/usr/bin/env node
/*
  Generate reference Markdown from Java source JavaDoc.
  Strategy:
  - Walk ../src/main/java for .java files
  - Parse JavaDoc blocks and signatures with a simple heuristic
  - Write Markdown files into src/content/reference

  Note: This is a lightweight parser and may not cover all edge cases.
*/
import fs from 'node:fs/promises';
import path from 'node:path';
import { globby } from 'globby';

const repoRoot = path.resolve(process.cwd(), '..');
const javaRoot = path.join(repoRoot, 'src', 'main', 'java');
const outDir = path.resolve('src/content/reference');

/** Extract package, class/interface name, and JavaDoc from a java file */
async function parseJavaFile(file) {
  const text = await fs.readFile(file, 'utf8');
  const pkgMatch = text.match(/package\s+([\w\.]+);/);
  const pkg = pkgMatch?.[1] ?? '';

  // Class/interface/enum/record header (allow newlines using [\s\S])
  const headerMatch = text.match(/(?:\/\*\*([\s\S]*?)\*\/\s*)?(public\s+)?(final\s+)?(abstract\s+)?(class|interface|enum|record)\s+(\w+)/);
  if (!headerMatch) return null;
  const javadoc = (headerMatch[1] || '').replace(/^\s*\*\s?/gm, '').trim();
  const kind = headerMatch[5];
  const name = headerMatch[6];
  const fqcn = pkg ? `${pkg}.${name}` : name;

  // Methods (public/protected) with JavaDoc
  const methods = [];
  const methodRegex = /(?:\/\*\*([\s\S]*?)\*\/\s*)?(?:public|protected)\s+[\w\<\>\[\]]+\s+(\w+)\s*\(([^)]*)\)\s*(?:throws\s+[^{]+)?\{/g;
  let m;
  while ((m = methodRegex.exec(text))) {
    const md = (m[1] || '').replace(/^\s*\*\s?/gm, '').trim();
    const methodName = m[2];
    const params = m[3].trim();
    methods.push({ name: methodName, javadoc: md, params });
  }

  return { pkg, kind, name, fqcn, javadoc, methods };
}

function mdEscape(s){
  return s.replace(/[<>]/g, (c) => c === '<' ? '&lt;' : '&gt;');
}

function toMarkdown(info){
  let md = `---\ntitle: ${info.name}\nfqcn: ${info.fqcn}\npackage: ${info.pkg}\nkind: ${info.kind}\n---\n\n`;
  if (info.javadoc) md += info.javadoc + '\n\n';
  md += `## ${info.kind} ${info.name}\n\n`;
  if (info.methods.length) {
    md += '### Methods\n\n';
    for (const m of info.methods) {
      md += `- \`${m.name}(${mdEscape(m.params)})\`\n`;
      if (m.javadoc) md += `  - ${m.javadoc}\n`;
    }
  }
  return md;
}

async function main(){
  const files = await globby('**/*.java', { cwd: javaRoot, absolute: true });
  await fs.mkdir(outDir, { recursive: true });
  const entries = [];
  for (const f of files) {
    const info = await parseJavaFile(f);
    if (!info) continue;
    entries.push(info);
    const relDir = info.pkg.replace(/\./g, '/');
    const targetDir = path.join(outDir, relDir);
    await fs.mkdir(targetDir, { recursive: true });
    const filePath = path.join(targetDir, `${info.name}.md`);
    await fs.writeFile(filePath, toMarkdown(info));
  }
  console.log(`Generated ${entries.length} reference pages.`);
}

main().catch((e)=>{ console.error(e); process.exit(1); });
