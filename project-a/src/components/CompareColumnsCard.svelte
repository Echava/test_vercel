<script>
  import { headersStore } from '../stores/headersStore.js';
  import { rulesStore } from '../stores/rulesStores.js';
  import { writable } from 'svelte/store';
  import { ChevronDown, ChevronUp } from 'lucide-svelte';

  // Store local para manejar las comparaciones entre columnas
  let comparisons = writable([]);
  let selectedHeader1 = ''; // Cabecera seleccionada 1
  let selectedHeader2 = ''; // Cabecera seleccionada 2
  let selectedOperator = ''; // Operador seleccionado para comparar las cabeceras
  let isExpanded = false; // Estado de expansión del componente

  // Suscribirse al store global de cabeceras
  $: headers = $headersStore;

  // Añadir una nueva comparación entre columnas
  const addComparison = () => {
    if (selectedHeader1 && selectedHeader2 && selectedOperator) {
      comparisons.update(comps => [...comps, { header1: selectedHeader1, header2: selectedHeader2, operator: selectedOperator }]);
      // Resetear las selecciones después de añadir una comparación
      selectedHeader1 = '';
      selectedHeader2 = '';
      selectedOperator = '';
      updateRulesStore();
    }
  };

  // Eliminar una comparación existente por su índice
  const removeComparison = (index) => {
    comparisons.update(comps => comps.filter((_, i) => i !== index));
    updateRulesStore();
  };

  // Alternar el estado de expansión del componente
  const toggleExpand = () => {
    isExpanded = !isExpanded;
  };

  // Actualizar el rulesStore para reflejar las comparaciones añadidas
  function updateRulesStore() {
    rulesStore.update(rules => ({
      ...rules,
      rules: {
        ...rules.rules,
        categories: {
          ...rules.rules.categories,
          comparisonsWithOtherColumnRules: $comparisons
        }
      }
    }));
  }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
  <div class="flex items-center justify-between">
    <h2 class="text-lg font-bold text-white">Comparar Columnas</h2>
    <!-- Botón para expandir o contraer el componente -->
    <button on:click={toggleExpand} class="text-white hover:text-blue-300 transition-colors">
      {#if isExpanded}
        <ChevronUp />
      {:else}
        <ChevronDown />
      {/if}
    </button>
  </div>
  
  <!-- Mostrar la funcionalidad completa solo si el componente está expandido -->
  {#if isExpanded}
    <div class="flex flex-col sm:flex-row gap-4 mb-4 mt-4">
      <!-- Selector para la primera cabecera -->
      <select
        bind:value={selectedHeader1}
        class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
        aria-label="Seleccionar primera cabecera"
      >
        <option value="">Seleccionar cabecera 1</option>
        {#each headers as header}
          <option value={header}>{header}</option>
        {/each}
      </select>
  
      <!-- Selector para el operador de comparación -->
      <select
        bind:value={selectedOperator}
        class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
        aria-label="Seleccionar operador"
      >
        <option value="">Seleccionar operador</option>
        <option value="<">Menor que (&lt;)</option>
        <option value=">">Mayor que (&gt;)</option>
        <option value="=">Igual a (=)</option>
      </select>
  
      <!-- Selector para la segunda cabecera -->
      <select
        bind:value={selectedHeader2}
        class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
        aria-label="Seleccionar segunda cabecera"
      >
        <option value="">Seleccionar cabecera 2</option>
        {#each headers as header}
          <option value={header}>{header}</option>
        {/each}
      </select>
    </div>
  
    <!-- Botón para añadir una nueva comparación -->
    <button
      on:click={addComparison}
      class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors w-full mb-4"
      disabled={!selectedHeader1 || !selectedHeader2 || !selectedOperator}
    >
      Añadir Comparación
    </button>
  
    <!-- Mostrar la lista de comparaciones añadidas -->
    <div class="space-y-2">
      {#each $comparisons as comparison, index (index)}
        <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
          <span class="text-white">{comparison.header1} {comparison.operator} {comparison.header2}</span>
          <!-- Botón para eliminar una comparación específica -->
          <button
            on:click={() => removeComparison(index)}
            class="bg-red-500 text-white p-1 rounded-md hover:bg-red-600 transition-colors"
            aria-label={`Eliminar comparación ${comparison.header1} ${comparison.operator} ${comparison.header2}`}
          >
            Eliminar
          </button>
        </div>
      {/each}
    </div>
  {/if}
</div>

