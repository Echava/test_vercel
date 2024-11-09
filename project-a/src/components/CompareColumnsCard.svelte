<script>
  import { headersStore } from '../stores/headersStore.js';
  import { writable } from 'svelte/store';

  let comparisons = writable([]);
  let selectedHeader1 = '';
  let selectedHeader2 = '';
  let selectedOperator = '';

  $: headers = $headersStore;
  
  const addComparison = () => {
    if (selectedHeader1 && selectedHeader2 && selectedOperator) {
      comparisons.update(comps => [...comps, { header1: selectedHeader1, header2: selectedHeader2, operator: selectedOperator }]);
      selectedHeader1 = '';
      selectedHeader2 = '';
      selectedOperator = '';
    }
  };

  const removeComparison = (index) => {
    comparisons.update(comps => comps.filter((_, i) => i !== index));
  };
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
  <h2 class="text-lg font-bold mb-4 text-white">Comparar Columnas</h2>
  
  <div class="flex flex-col sm:flex-row gap-4 mb-4">
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

  <button
    on:click={addComparison}
    class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors w-full mb-4"
    disabled={!selectedHeader1 || !selectedHeader2 || !selectedOperator}
  >
    Añadir Comparación
  </button>

  <div class="space-y-2">
    {#each $comparisons as comparison, index (index)}
      <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
        <span class="text-white">{comparison.header1} {comparison.operator} {comparison.header2}</span>
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
</div>