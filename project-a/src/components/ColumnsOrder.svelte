<script>
    import { headersStore } from '../stores/headersStore.js';
    import { writable } from 'svelte/store';
  
    let selectedColumns = writable([{ id: 1, value: '' }]);
    let orderType = '';
    let nextId = 2;
    let isExpanded = false; // Estado de expansión del componente
  
    $: headers = $headersStore;
    $: canAddColumn = $selectedColumns.length < 5;
  
    // Función para añadir una nueva columna
    function addColumn() {
      if (canAddColumn) {
        selectedColumns.update(cols => [...cols, { id: nextId, value: '' }]);
        nextId++;
      }
    }
  
    // Función para eliminar una columna existente
    function removeColumn(id) {
      selectedColumns.update(cols => cols.filter(col => col.id !== id));
    }
  
    // Manejar el cambio de orden
    function handleOrderChange() {
      console.log('Orden seleccionado:', orderType);
    }
  
    // Manejar el cambio en las columnas seleccionadas
    function handleColumnsChange() {
      console.log('Columnas seleccionadas:', $selectedColumns.map(col => col.value));
    }
  
    // Alternar el estado de expansión del componente
    function toggleExpand() {
      isExpanded = !isExpanded;
    }
  </script>
  
  <div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-bold text-white">Orden En Columnas</h2>
      <!-- Botón para expandir o contraer el componente -->
      <button on:click={toggleExpand} class="text-white focus:outline-none">
        {isExpanded ? '▲' : '▼'}
      </button>
    </div>
    
    {#if isExpanded}
      <div class="space-y-4 mt-4">
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
    
      {#if canAddColumn}
        <button
          on:click={addColumn}
          class="mt-4 bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors w-full"
        >
          Agregar Columna
        </button>
      {/if}
    
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
