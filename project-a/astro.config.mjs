import { defineConfig } from 'astro/config';
import vercel from '@astrojs/vercel/serverless';
import svelte from '@astrojs/svelte';
import tailwind from '@astrojs/tailwind';

export default defineConfig({
  output: 'server',
  adapter: vercel({
    analytics: true,
    webAnalytics: {
      enabled: true,
    },
    devCommand: 'astro dev',
    buildCommand: 'astro build',
  }),
  integrations: [svelte(), tailwind()],
  
});