<script>
    import { headersStore } from '../stores/headersStore.js';
    import { writable } from 'svelte/store';
  
    // Store local para manejar las comparaciones
    let comparisons = writable([]);
    let showHeaders = false;
    let selectedHeader1 = '';
    let selectedHeader2 = '';
    let selectedOperator = '';
  
    // Obtener las cabeceras del store global
    $: headers = $headersStore;
    
    // Función para añadir una nueva comparación
    const addComparison = () => {
      if (selectedHeader1 && selectedHeader2 && selectedOperator) {
        comparisons.update(comps => [...comps, { header1: selectedHeader1, header2: selectedHeader2, operator: selectedOperator }]);
        // Resetear las selecciones después de añadir
        selectedHeader1 = '';
        selectedHeader2 = '';
        selectedOperator = '';
      }
    };
  
    // Función para eliminar una comparación existente
    const removeComparison = (index) => {
      comparisons.update(comps => comps.filter((_, i) => i !== index));
    };
  </script>
  
  <div class="category-card p-4 border rounded-md shadow-md bg-zinc-300 w-full sm:max-w-sm">
    <h3 class="font-bold text-lg text-zinc-800">Comparar Columnas</h3>
    <!-- Botón para mostrar/ocultar la sección de selección -->
    <button 
      class="mt-2 bg-blue-500 text-white p-2 rounded hover:bg-blue-700" 
      on:click={() => showHeaders = !showHeaders}
    >
      {showHeaders ? 'Ocultar' : 'Seleccionar'}
    </button>
    
    {#if showHeaders}
      <div class="mt-2 space-y-2">
        <!-- Selector para la primera cabecera -->
        <select 
          bind:value={selectedHeader1}
          class="bg-gray-200 text-black p-2 rounded w-full"
        >
          <option value="" disabled selected>Seleccionar cabecera 1</option>
          {#each headers as header (header)}
            <option value={header}>{header}</option>
          {/each}
        </select>
        
        <!-- Selector para el operador de comparación -->
        <select 
          bind:value={selectedOperator}
          class="bg-gray-200 text-black p-2 rounded w-full"
        >
          <option value="" disabled selected>Seleccionar operador</option>
          <option value="<">Menor que (&lt;)</option>
          <option value=">">Mayor que (&gt;)</option>
          <option value="=">Igual a (=)</option>
        </select>
        
        <!-- Selector para la segunda cabecera -->
        <select 
          bind:value={selectedHeader2}
          class="bg-gray-200 text-black p-2 rounded w-full"
        >
          <option value="" disabled selected>Seleccionar cabecera 2</option>
          {#each headers as header (header)}
            <option value={header}>{header}</option>
          {/each}
        </select>
        
        <!-- Botón para añadir la comparación -->
        <button 
          class="bg-green-500 text-white p-2 rounded hover:bg-green-700 w-full"
          on:click={addComparison}
        >
          Añadir Comparación
        </button>
      </div>
    {/if}
  
    <!-- Lista de comparaciones añadidas -->
    <div class="mt-4">
      <h4 class="font-semibold text-zinc-500">Comparaciones:</h4>
      {#each $comparisons as comparison, index (index)}
        <div class="flex items-center justify-between mt-1 bg-white p-2 rounded">
          <span class="text-zinc-800">{comparison.header1} {comparison.operator} {comparison.header2}</span>
          <button 
            class="bg-red-500 text-white p-1 rounded hover:bg-red-700"
            on:click={() => removeComparison(index)}
          >
            Eliminar
          </button>
        </div>
      {/each}
    </div>
  </div>
  
  <style>
    /* Estilo para mantener consistencia con CategoryCard */
    .category-card {
      max-width: 300px;
    }
  </style>