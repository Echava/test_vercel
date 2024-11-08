<!-- <script>
  import CategoryCard from './CategoryCard.svelte';
  import CompareColumnsCard from './CompareColumnsCard.svelte';
  import { categoriesStore } from '../stores/categoriesStore.js';
  import HeaderTypeAssociation from './HeaderTypeAssociation.svelte';

  let categories = [];

  // Suscribirse al store para obtener las categorías
  $: categories = $categoriesStore;
</script>

<div class="w-full grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
  {#each categories as category}
      <CategoryCard {category} />
  {/each}
  <CompareColumnsCard />

  <HeaderTypeAssociation />
</div> -->
  
<script>
  import CategoryCard from './CategoryCard.svelte';
  import CompareColumnsCard from './CompareColumnsCard.svelte';
  import HeaderTypeAssociation from './HeaderTypeAssociation.svelte';
  import { categoriesStore } from '../stores/categoriesStore.js';

  let categories = [];

  // Suscribirse al store para obtener las categorías
  $: categories = $categoriesStore;
</script>

<div class="space-y-6">
  <div class="bg-zinc-300 p-4 rounded-lg">
    <HeaderTypeAssociation />
  </div>

  <div class="w-full grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
    {#each categories as category}
      <CategoryCard {category} />
    {/each}
    <CompareColumnsCard />
  </div>
</div>