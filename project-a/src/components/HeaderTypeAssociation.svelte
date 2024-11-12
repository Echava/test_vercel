<script>
    import { headersStore } from '../stores/headersStore.js';
    import { variableTypes } from '../stores/variableTypes.js';
    import { writable } from 'svelte/store';
  
    let selectedHeader = ''; // Cabecera seleccionada para asociar
    let selectedType = ''; // Tipo de variable seleccionada para la cabecera
    const associations = writable([]); // Store local para manejar las asociaciones entre cabeceras y tipos
    let isExpanded = false; // Estado de expansión del componente
  
    // Obtener las cabeceras del store global
    $: headers = $headersStore;
  
    // Función para añadir una asociación entre una cabecera y un tipo de variable
    function addAssociation() {
      if (selectedHeader && selectedType) {
        associations.update(assocs => {
          // Verificar si la cabecera ya está asociada antes de añadirla
          if (!assocs.some(a => a.header === selectedHeader)) {
            return [...assocs, { header: selectedHeader, type: selectedType }];
          }
          return assocs;
        });
        // Resetear las selecciones después de añadir una asociación
        selectedHeader = '';
        selectedType = '';
      }
    }
  
    // Función para eliminar una asociación existente
    function removeAssociation(header) {
      associations.update(assocs => assocs.filter(a => a.header !== header));
    }

    // Función para alternar el estado de expansión del componente
    function toggleExpand() {
      isExpanded = !isExpanded;
    }
  </script>
  
  <div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-bold text-white">Asociación de Cabeceras y Tipos</h2>
      <!-- Botón para expandir o contraer el componente -->
      <button on:click={toggleExpand} class="text-white focus:outline-none">
        {isExpanded ? '▲' : '▼'}
      </button>
    </div>
    
    {#if isExpanded}
      <div class="flex flex-col sm:flex-row gap-4 mb-4 mt-4">
        <!-- Selector para la cabecera a asociar -->
        <select
          bind:value={selectedHeader}
          class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
          aria-label="Seleccionar cabecera"
        >
          <option value="">Seleccionar cabecera</option>
          {#each headers as header}
            <option value={header}>{header}</option>
          {/each}
        </select>
    
        <!-- Selector para el tipo de variable a asociar con la cabecera -->
        <select
          bind:value={selectedType}
          class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
          aria-label="Seleccionar tipo de variable"
        >
          <option value="">Seleccionar tipo</option>
          {#each variableTypes as type}
            <option value={type}>{type}</option>
          {/each}
        </select>
    
        <!-- Botón para agregar una nueva asociación -->
        <button
          on:click={addAssociation}
          class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors"
          disabled={!selectedHeader || !selectedType}
        >
          Agregar
        </button>
      </div>
    
      <!-- Mostrar la lista de asociaciones añadidas -->
      <div class="space-y-2">
        {#each $associations as association (association.header)}
          <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
            <span class="text-white">{association.header}: {association.type}</span>
            <!-- Botón para eliminar una asociación específica -->
            <button
              on:click={() => removeAssociation(association.header)}
              class="bg-red-500 text-white p-1 rounded-md hover:bg-red-600 transition-colors"
              aria-label={`Eliminar asociación para ${association.header}`}
            >
              Eliminar
            </button>
          </div>
        {/each}
      </div>
    {/if}
  </div>
