<script>
    import { headersStore } from '../stores/headersStore.js';
    import { onMount } from 'svelte';
    import { writable } from 'svelte/store';
  
    export let category;
    let associatedHeaders = writable([]);
    let showHeaders = false;
  
    // Suscribirse al headersStore para poder desplegar las cabeceras disponibles
    let headers = [];
    onMount(() => {
      headersStore.subscribe(value => {
        headers = value;
      });
    });
  
    // Función para asociar una cabecera a la categoría
    const addHeaderToCategory = (header) => {
      associatedHeaders.update(headers => [...headers, header]);
    };
  
    // Función para eliminar una cabecera asociada
    const removeHeaderFromCategory = (header) => {
      associatedHeaders.update(headers => headers.filter(h => h !== header));
    };
  </script>
  
  <div class="category-card p-4 border rounded-md shadow-md bg-zinc-300">
    <h3 class="font-bold text-lg text-zinc-800">{category.name}</h3>
    <button class="mt-2 bg-blue-500 text-white p-2 rounded hover:bg-blue-700" on:click={() => showHeaders = !showHeaders}>+</button>
    
    <!-- Desplegar lista de cabeceras disponibles -->
    {#if showHeaders}
      <div class="available-headers mt-2">
        {#each headers as header}
          <button class="bg-gray-200 text-black p-1 rounded m-1" on:click={() => addHeaderToCategory(header)}>{header}</button>
        {/each}
      </div>
    {/if}
  
    <!-- Mostrar cabeceras asociadas -->
    <div class="associated-headers mt-4">
      <h4 class="font-semibold text-zinc-500">Cabeceras Asociadas:</h4>
      {#each $associatedHeaders as header}
        <div class="associated-header-item flex items-center mt-1">
          <span class="mr-2">{header}</span>
          <button class="bg-red-500 text-white p-1 rounded" on:click={() => removeHeaderFromCategory(header)}>-</button>
        </div>
      {/each}
    </div>
  </div>
  
  <style>
    .category-card {
      max-width: 300px;
    }
  </style>
  
  