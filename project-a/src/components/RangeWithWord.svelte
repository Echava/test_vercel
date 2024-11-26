<script>
  import { headersStore } from '../stores/headersStore.js';
  import { rulesStore } from '../stores/rulesStores.js';
  import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

  let selectedBaseColumn = '';
  let selectedTypeColumn = '';
  let isExpanded = false;
  let errorMessage = '';

  let newMinInteger = '';
  let newMinDecimal = '';
  let newMaxInteger = '';
  let newMaxDecimal = '';
  let newRangeType = '';

  $: headers = $headersStore;
  $: ranges = $rulesStore.categories?.rangeWithWordRules?.[0]?.ranges || [];

  function addRange() {
    const min = parseFloat(`${newMinInteger}.${newMinDecimal}`);
    const max = newMaxInteger || newMaxDecimal ? parseFloat(`${newMaxInteger}.${newMaxDecimal}`) : null;

    if (isNaN(min) || (max !== null && isNaN(max))) {
      errorMessage = 'Ingrese valores numéricos válidos.';
      return;
    }

    if (max !== null && min > max) {
      errorMessage = 'El valor mínimo debe ser menor o igual al valor máximo.';
      return;
    }

    if (!newRangeType) {
      errorMessage = 'Por favor, ingrese un valor para "Palabra Clave".';
      return;
    }

    if (ranges.some(range => 
      (min >= range.min && min <= range.max) || 
      (max !== null && max >= range.min && max <= range.max) ||
      (min <= range.min && (max === null || max >= range.max))
    )) {
      errorMessage = 'El nuevo rango se solapa con un rango existente.';
      return;
    }

    updateRulesStore([...ranges, { min, max, type: newRangeType }]);

    newMinInteger = newMinDecimal = newMaxInteger = newMaxDecimal = newRangeType = '';
    errorMessage = '';
  }

  function removeRange(index) {
    updateRulesStore(ranges.filter((_, i) => i !== index));
  }

  function updateRulesStore(updatedRanges) {
    if (selectedBaseColumn && selectedTypeColumn) {
      rulesStore.update(rules => ({
        ...rules,
        categories: {
          ...rules.categories,
          rangeWithWordRules: [
            {
              rangeColumn: selectedBaseColumn,
              typeColumn: selectedTypeColumn,
              ranges: updatedRanges
            }
          ]
        }
      }));
    }
  }

  $: {
    if (selectedBaseColumn && selectedTypeColumn) {
      updateRulesStore(ranges);
    }
  }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
  <div class="flex justify-between items-center">
    <h2 class="text-lg font-bold text-white">Rangos con Palabras Clave</h2>
    <button 
      on:click={() => isExpanded = !isExpanded}
      class="text-white hover:text-blue-300 transition-colors"
      aria-label={isExpanded ? "Contraer" : "Expandir"}
    >
      {#if isExpanded}
        <ChevronUp />
      {:else}
        <ChevronDown />
      {/if}
    </button>
  </div>

  {#if isExpanded}
    <div class="mb-4">
      <label for="base-column-select" class="block text-white mb-2">Columna Base:</label>
      <select
        id="base-column-select"
        bind:value={selectedBaseColumn}
        class="w-full bg-zinc-600 text-white p-2 rounded-md"
        disabled={selectedBaseColumn !== ''}
      >
        <option value="">Seleccionar columna</option>
        {#each headers as header}
          <option value={header}>{header}</option>
        {/each}
      </select>
    </div>

    <div class="mb-4">
      <label for="type-column-select" class="block text-white mb-2">Columna Tipo:</label>
      <select
        id="type-column-select"
        bind:value={selectedTypeColumn}
        class="w-full bg-zinc-600 text-white p-2 rounded-md"
        disabled={selectedTypeColumn !== ''}
      >
        <option value="">Seleccionar columna</option>
        {#each headers as header}
          <option value={header}>{header}</option>
        {/each}
      </select>
    </div>

    <div class="flex flex-wrap gap-2 mb-4">
      <div class="flex-1 min-w-[120px]">
        <label for="min-integer" class="block text-white mb-1">Mínimo:</label>
        <div class="flex">
          <input
            id="min-integer"
            type="number"
            bind:value={newMinInteger}
            placeholder="Entero"
            class="w-1/2 bg-zinc-600 text-white p-2 rounded-l-md"
          />
          <span class="bg-zinc-500 text-white p-2">.</span>
          <input
            type="number"
            bind:value={newMinDecimal}
            placeholder="Decimal"
            class="w-1/2 bg-zinc-600 text-white p-2 rounded-r-md"
          />
        </div>
      </div>
      <div class="flex-1 min-w-[120px]">
        <label for="max-integer" class="block text-white mb-1">Máximo:</label>
        <div class="flex">
          <input
            id="max-integer"
            type="number"
            bind:value={newMaxInteger}
            placeholder="Entero"
            class="w-1/2 bg-zinc-600 text-white p-2 rounded-l-md"
          />
          <span class="bg-zinc-500 text-white p-2">.</span>
          <input
            type="number"
            bind:value={newMaxDecimal}
            placeholder="Decimal"
            class="w-1/2 bg-zinc-600 text-white p-2 rounded-r-md"
          />
        </div>
      </div>
      <div class="flex-1 min-w-[120px]">
        <label for="range-type" class="block text-white mb-1">Palabra Clave:</label>
        <input
          id="range-type"
          type="text"
          bind:value={newRangeType}
          placeholder="Palabra Clave"
          class="w-full bg-zinc-600 text-white p-2 rounded-md"
        />
      </div>
    </div>

    <button
      on:click={addRange}
      class="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors mb-4"
    >
      Agregar Rango
    </button>

    {#if errorMessage}
      <p class="text-red-500 mb-4">{errorMessage}</p>
    {/if}

    <div class="space-y-2">
      {#each ranges as range, index}
        <div class="flex items-center bg-zinc-600/50 p-2 rounded-md">
          <span class="text-white flex-grow">
            {range.min} - {range.max === null ? '∞' : range.max} ({range.type})
          </span>
          <button
            on:click={() => removeRange(index)}
            class="text-red-400 hover:text-red-600 transition-colors"
            aria-label={`Eliminar rango ${range.type}`}
          >
            <X size={20} />
          </button>
        </div>
      {/each}
    </div>
  {/if}
</div>


      