import { defineConfig } from 'astro/config';
import tailwind from "@astrojs/tailwind";
import svelte from "@astrojs/svelte"; // Importa la integración de Svelte
import node from '@astrojs/node';

// https://astro.build/config
export default defineConfig({
  integrations: [tailwind(), svelte()],  // Agrega la integración de Svelte
  output: 'server', 
  adapter: node()
});