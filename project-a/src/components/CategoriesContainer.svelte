<script>
    import CategoryCard from './CategoryCard.svelte';
    import { categoriesStore } from '../stores/categoriesStore.js';
  
    let categories = [];
  
    // Suscribirse al store para obtener las categorías
    $: categories = $categoriesStore;
  </script>
  
  <div class="grid grid-cols-1 gap-4 p-4">
    {#each categories as category}
      <CategoryCard {category} />
    {/each}
  </div>
  