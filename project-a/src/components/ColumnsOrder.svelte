<script>
    import { headersStore } from '../stores/headersStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { writable } from 'svelte/store';
    import { ChevronDown, ChevronUp } from 'lucide-svelte';
  
    let selectedColumns = writable([{ id: 1, value: '' }]);
    let orderType = '';
    let nextId = 2;
    let isExpanded = false; // Estado de expansión del componente
  
    // Obtener las cabeceras del store global
    $: headers = $headersStore;
    $: canAddColumn = $selectedColumns.length < 5;
  
    // Añadir una nueva columna al listado
    function addColumn() {
      if (canAddColumn) {
        selectedColumns.update(cols => [...cols, { id: nextId, value: '' }]);
        nextId++;
        updateRulesStore();
      }
    }
  
    // Eliminar una columna específica por ID
    function removeColumn(id) {
      selectedColumns.update(cols => cols.filter(col => col.id !== id));
      updateRulesStore();
    }
  
    // Cambiar el orden de las columnas y actualizar el store
    function handleOrderChange() {
      updateRulesStore();
    }
  
    // Cambiar los valores de las columnas seleccionadas
    function handleColumnsChange() {
      updateRulesStore();
    }
  
    // Alternar la expansión del componente
    const toggleExpand = () => {
      isExpanded = !isExpanded;
    };
  
    // Actualizar el rulesStore para reflejar las reglas de orden de columnas
    function updateRulesStore() {
      rulesStore.update(rules => ({
        ...rules,
        rules: {
          ...rules.rules,
          categories: {
            ...rules.rules.categories,
            orderColumnsRules: [
              {
                columns: $selectedColumns.map(col => col.value),
                orderType
              }
            ]
          }
        }
      }));
    }
  </script>
  
  <div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-bold text-white">Ordenar Columnas</h2>
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
      <div class="space-y-4 mb-4 mt-4">
        {#each $selectedColumns as column (column.id)}
          <div class="flex flex-col sm:flex-row gap-2 items-center">
            <label for="column-{column.id}" class="text-white w-24">Columna {column.id}</label>
            <select
              id="column-{column.id}"
              bind:value={column.value}
              on:change={handleColumnsChange}
              class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
              aria-label={`Seleccionar columna ${column.id}`}
            >
              <option value="">Seleccionar columna</option>
              {#each headers as header}
                <option value={header}>{header}</option>
              {/each}
            </select>
            {#if $selectedColumns.length > 1}
              <button
                on:click={() => removeColumn(column.id)}
                class="bg-red-500 text-white p-2 rounded-md hover:bg-red-600 transition-colors"
                aria-label={`Eliminar columna ${column.id}`}
              >
                Eliminar
              </button>
            {/if}
          </div>
        {/each}
      </div>
  
      <!-- Botón para añadir una nueva columna -->
      {#if canAddColumn}
        <button
          on:click={addColumn}
          class="mt-4 bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors w-full"
        >
          Agregar Columna
        </button>
      {/if}
  
      <!-- Selector para definir el tipo de orden -->
      <div class="mt-4">
        <label for="order-type" class="text-white block mb-2">Tipo de Orden</label>
        <select
          id="order-type"
          bind:value={orderType}
          on:change={handleOrderChange}
          class="bg-zinc-600 text-white p-2 rounded-md w-full"
          aria-label="Seleccionar tipo de orden"
        >
          <option value="">Seleccionar orden</option>
          <option value="asc">Orden Ascendente</option>
          <option value="desc">Orden Descendente</option>
        </select>
      </div>
    {/if}
  </div>
  