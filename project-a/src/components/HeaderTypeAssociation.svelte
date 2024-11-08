<script>
    import { headersStore } from '../stores/headersStore.js';
    import { variableTypes } from '../stores/variableTypes.js';
    import { writable } from 'svelte/store';
  
    let selectedHeader = '';
    let selectedType = '';
    const associations = writable([]);
  
    $: headers = $headersStore;
  
    function addAssociation() {
      if (selectedHeader && selectedType) {
        associations.update(assocs => {
          if (!assocs.some(a => a.header === selectedHeader)) {
            return [...assocs, { header: selectedHeader, type: selectedType }];
          }
          return assocs;
        });
        selectedHeader = '';
        selectedType = '';
      }
    }
  
    function removeAssociation(header) {
      associations.update(assocs => assocs.filter(a => a.header !== header));
    }
  </script>
  
  <div class="bg-zinc-700/50 p-4 rounded-lg">
    <h2 class="text-lg font-bold mb-4 text-white">Asociación de Cabeceras y Tipos</h2>
    
    <div class="flex flex-col sm:flex-row gap-4 mb-4">
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
  
      <button
        on:click={addAssociation}
        class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors"
        disabled={!selectedHeader || !selectedType}
      >
        Agregar
      </button>
    </div>
  
    <div class="space-y-2">
      {#each $associations as association (association.header)}
        <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
          <span class="text-white">{association.header}: {association.type}</span>
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
  </div>