<script>
  import { headersStore } from '../stores/headersStore.js';
  import { rulesStore } from '../stores/rulesStores.js';
  import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

  let selectedHeader1 = '';
  let selectedHeader2 = '';
  let selectedOperator = '';
  let isExpanded = false;

  $: headers = $headersStore;
  $: comparisons = $rulesStore.categories?.comparisonsWithOtherColumnRules || [];

  const addComparison = () => {
    if (selectedHeader1 && selectedHeader2 && selectedOperator) {
      rulesStore.update(rules => ({
        ...rules,
        categories: {
          ...rules.categories,
          comparisonsWithOtherColumnRules: [
            ...(rules.categories?.comparisonsWithOtherColumnRules || []),
            { header1: selectedHeader1, header2: selectedHeader2, operator: selectedOperator }
          ]
        }
      }));
      selectedHeader1 = '';
      selectedHeader2 = '';
      selectedOperator = '';
    }
  };

  const removeComparison = (index) => {
    rulesStore.update(rules => ({
      ...rules,
      categories: {
        ...rules.categories,
        comparisonsWithOtherColumnRules: rules.categories?.comparisonsWithOtherColumnRules.filter((_, i) => i !== index)
      }
    }));
  };

  const toggleExpand = () => {
    isExpanded = !isExpanded;
  };
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
  <div class="flex items-center justify-between">
    <h2 class="text-lg font-bold text-white">Comparar Columnas</h2>
    <button on:click={toggleExpand} class="text-white hover:text-blue-300 transition-colors">
      {#if isExpanded}
        <ChevronUp />
      {:else}
        <ChevronDown />
      {/if}
    </button>
  </div>
  
  {#if isExpanded}
    <div class="flex flex-col sm:flex-row gap-4 mb-4 mt-4">
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
      {#each comparisons as comparison, index (index)}
        <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
          <span class="text-white">{comparison.header1} {comparison.operator} {comparison.header2}</span>
          <button
            on:click={() => removeComparison(index)}
            class="text-red-400 hover:text-red-600 transition-colors"
            aria-label={`Eliminar comparación ${comparison.header1} ${comparison.operator} ${comparison.header2}`}
          >
            <X size={20} />
          </button>
        </div>
      {/each}
    </div>
  {/if}
</div>

