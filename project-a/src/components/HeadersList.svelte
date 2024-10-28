<script>
    import { headersStore } from "../stores/headersStore.js";
    import { onMount } from "svelte";
    import { get } from "svelte/store";
  
    let headers = [];
  
    // Suscribirse al store para obtener las cabeceras
    onMount(() => {
      headersStore.subscribe((value) => {
        headers = value;
      });
    });
  </script>
  
  <div class="mt-4">
    <h2 class="text-lg font-bold">Cabeceras del Archivo:</h2>
    {#if headers.length > 0}
      <ul class="list-disc list-inside mt-2">
        {#each headers as header}
          <li>{header}</li>
        {/each}
      </ul>
    {:else}
      <p class="text-gray-500">No se ha cargado ningún archivo.</p>
    {/if}
  </div>
  