import { defineConfig } from 'astro/config';
import tailwind from "@astrojs/tailwind";
import svelte from "@astrojs/svelte"; // Importa la integración de Svelte
import vercel from '@astrojs/vercel/serverless';

// https://astro.build/config
export default defineConfig({
  integrations: [tailwind(), svelte()],  // Agrega la integración de Svelte
  output: 'server', 
  adapter: vercel()
});