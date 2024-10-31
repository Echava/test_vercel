<script>
    import CategoryCard from './CategoryCard.svelte';
    import { categoriesStore } from '../stores/categoriesStore.js';
  
    let categories = [];
  
    // Suscribirse al store para obtener las categorías
    $: categories = $categoriesStore;
  </script>
  
  <div class="w-full grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
    {#each categories as category}
      <CategoryCard {category} />
    {/each}
  </div>
  