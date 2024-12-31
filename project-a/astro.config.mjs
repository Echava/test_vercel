import { defineConfig } from 'astro/config';
import vercel from '@astrojs/vercel/serverless';
import svelte from '@astrojs/svelte';
import tailwind from '@astrojs/tailwind';

export default defineConfig({
  output: 'server',
  outDir: './dist',
  adapter: vercel(),
  integrations: [svelte(), tailwind()]
});