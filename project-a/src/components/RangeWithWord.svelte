<script>
  import { headersStore } from '../stores/headersStore.js';
  import { rulesStore } from '../stores/rulesStores.js';
  import { writable } from 'svelte/store';
  import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

  let selectedBaseColumn = ''; // Columna base seleccionada
  let selectedTypeColumn = ''; // Columna tipo seleccionada
  let isExpanded = false;
  let errorMessage = '';

  // Valores para el nuevo rango
  let newMinInteger = '';
  let newMinDecimal = '';
  let newMaxInteger = '';
  let newMaxDecimal = '';
  let newRangeType = ''; // Tipo del rango ingresado por el usuario

  $: headers = $headersStore;

  // Store local para manejar los rangos
  const ranges = writable([]);

  // Función para agregar un nuevo rango
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

    // Verificar solapamiento
    if ($ranges.some(range => 
      (min >= range.min && min <= range.max) || 
      (max !== null && max >= range.min && max <= range.max) ||
      (min <= range.min && (max === null || max >= range.max))
    )) {
      errorMessage = 'El nuevo rango se solapa con un rango existente.';
      return;
    }

    ranges.update(r => [...r, { 
      min, 
      max, 
      type: newRangeType
    }]);

    // Limpiar los campos y el mensaje de error
    newMinInteger = newMinDecimal = newMaxInteger = newMaxDecimal = newRangeType = '';
    errorMessage = '';

    updateRulesStore();
  }

  // Función para eliminar un rango
  function removeRange(index) {
    ranges.update(r => r.filter((_, i) => i !== index));
    updateRulesStore();
  }

  // Función para actualizar el rulesStore
  function updateRulesStore() {
    if (selectedBaseColumn && selectedTypeColumn && $ranges.length > 0) {
      rulesStore.update(rules => ({
        ...rules,
        rules: {
          ...rules.rules,
          categories: {
            ...rules.rules.categories,
            rangeWithWordRules: [{
              rangeColumn: selectedBaseColumn,
              typeColumn: selectedTypeColumn,
              ranges: $ranges
            }]
          }
        }
      }));
    }
  }

  // Actualizar el store cuando cambia la columna seleccionada
  $: {
    if (selectedBaseColumn && selectedTypeColumn) {
      updateRulesStore();
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

    <!-- Selección de columna base -->
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

    <!-- Selección de columna tipo -->
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

    <!-- Inputs para nuevo rango -->
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

    <!-- Lista de rangos -->
    <div class="space-y-2">
      {#each $ranges as range, index}
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


      